package me.savvy.rixa.commands.general;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.savvy.rixa.commands.handlers.Command;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandType;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.modules.music.MusicManager;
import me.savvy.rixa.modules.music.MusicModule;
import me.savvy.rixa.modules.music.TrackScheduler;
import me.savvy.rixa.utils.MessageBuilder;
import me.savvy.rixa.utils.YoutubeSearch;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

/**
 * Created by Timber on 6/6/2017.
 */
public class MusicCommand implements CommandExec {

    private final int DEFAULT_VOLUME = 35; //(0 - 150, where 100 is default max volume)
    private final AudioPlayerManager playerManager;
    private final Map<String, MusicManager> musicManagers;
    public MusicCommand() {
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        musicManagers = new HashMap<>();
    }

    @Command(description = "Play music in your voice chat.",
            type = CommandType.USER,
            channelType = ChannelType.TEXT,
            usage = "%pmusic", mainCommand = "music")
    public void execute(GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        RixaGuild rixaGuild = Guilds.getGuild(guild);
        MusicModule module = ((MusicModule) rixaGuild.getModule("Music"));
        if(!module.isEnabled()) {
            new MessageBuilder("Sorry music is not enabled on `" + guild.getName() + "`!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        if(module.isRoleRequired()) {
            Role role = event.getGuild().getRoleById(module.getMusicRole());
            boolean hasRole = false;
            for (Role roleItem : event.getMember().getRoles()) {
                if (roleItem.getId().equalsIgnoreCase(role.getId())) {
                    hasRole = true;
                }
            }
            if(!hasRole) {
                new MessageBuilder("You must have the " + role.getName() + " role to use the music module.").setColor(event.getMember().getColor()).queue(event.getChannel());
                return;
            }
        }
        String[] message = event.getMessage().getContent().split(" ");
        MusicManager mng = getMusicManager(guild);
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;
        // music join <channel>
        if(message.length == 1) {
            sendHelp(event.getGuild().getId(), rixaGuild.getGuildSettings().getPrefix(), event.getAuthor(), event.getChannel());
        } else if (message.length == 2) {
            if(message[1].equalsIgnoreCase("join") || message[1].equalsIgnoreCase("summon") ) {
                if (event.getMember().getVoiceState().getChannel() == null) {
                    new MessageBuilder("You must be in a voice channel to summon me!").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                try {
                    guild.getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
                } catch (PermissionException e) {
                    if (e.getPermission() == Permission.VOICE_CONNECT) {
                        new MessageBuilder("I do not have permission to join the requested voice channel.").setColor(event.getMember().getColor()).queue(event.getChannel());
                    }
                }
            } else if (message[1].equalsIgnoreCase("reset")) {
                synchronized (musicManagers) {
                    scheduler.queue.clear();
                    player.destroy();
                    guild.getAudioManager().setSendingHandler(null);
                    musicManagers.remove(guild.getId());
                }
                mng = getMusicManager(guild);
                guild.getAudioManager().setSendingHandler(mng.sendHandler);
                new MessageBuilder("The player has been completely reset!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if (message[1].equalsIgnoreCase("skip")) {
                scheduler.nextTrack();
                new MessageBuilder("Successfully skipped current track.").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if(message[1].equalsIgnoreCase("play") || message[1].equalsIgnoreCase("resume")) {
                if (player.isPaused()) {
                    player.setPaused(false);
                    new MessageBuilder("MusicPlayer resumed track: " + player.getPlayingTrack().getInfo().title).setColor(event.getMember().getColor()).queue(event.getChannel());
                } else if (player.getPlayingTrack() != null) {
                    new MessageBuilder("MusicPlayer already playing track: " + player.getPlayingTrack().getInfo().title).setColor(event.getMember().getColor()).queue(event.getChannel());
                } else if (scheduler.queue.isEmpty()) {
                    new MessageBuilder("The audio queue is empty! Add a track to the queue first!").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            } else if (message[1].equalsIgnoreCase("leave")) {
                String desc = "";
                AudioTrack track = scheduler.nextTrack();
                if(track != null) {
                    desc += "Track skipped. ";
                }
                new MessageBuilder(desc + "Leaving voice channel...").setColor(event.getMember().getColor()).queue(event.getChannel());
                guild.getAudioManager().setSendingHandler(null);
                guild.getAudioManager().closeAudioConnection();
            } else if(message[1].equalsIgnoreCase("link")) {
                if (player.getPlayingTrack() == null) {
                    new MessageBuilder("There is no track currently playing.").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                new MessageBuilder("Track Name: " + player.getPlayingTrack().getInfo().title + "\n Track Link: " +
                        player.getPlayingTrack().getInfo().uri).setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if(message[1].equalsIgnoreCase("pause")) {
                if (player.getPlayingTrack() == null) {
                    new MessageBuilder("There is no track currently playing.").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                player.setPaused(!player.isPaused());
                if (player.isPaused()) {
                    new MessageBuilder("The music player has been paused.").setColor(event.getMember().getColor()).queue(event.getChannel());
                } else {
                    new MessageBuilder("There music player has resumed playing.").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            } else if(message[1].equalsIgnoreCase("stop")) {
                scheduler.queue.clear();
                player.stopTrack();
                player.setPaused(false);
                new MessageBuilder("The music player has been stopped and queue has been cleared.").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if(message[1].equalsIgnoreCase("vol") || message[1].equalsIgnoreCase("volume")) {
                new MessageBuilder("Music player volume is currently at: " + player.getVolume()).setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if(message[1].equalsIgnoreCase("restart")) {
                AudioTrack track = player.getPlayingTrack();
                if (track == null) {
                    track = scheduler.lastTrack;
                }
                if (track != null) {
                    new MessageBuilder("Restarting Track: " + track.getInfo().title).setColor(event.getMember().getColor()).queue(event.getChannel());
                    player.playTrack(track.makeClone());
                } else {
                    new MessageBuilder("No track has been previously played.").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            } else if(message[1].equalsIgnoreCase("repeat")) {
                scheduler.setRepeating(!scheduler.isRepeating());
                new MessageBuilder("Repeat on music play has been " + (scheduler.isRepeating() ? "enabled" : "disabled")).setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if(message[1].equalsIgnoreCase("np") || message[1].equalsIgnoreCase("nowplaying")) {
                AudioTrack currentTrack = player.getPlayingTrack();
                if (currentTrack != null) {
                    String title = currentTrack.getInfo().title;
                    String position = getTimestamp(currentTrack.getPosition());
                    String duration = getTimestamp(currentTrack.getDuration());
                    new MessageBuilder(String.format("Now Playing: %s [%s / %s]", title, position, duration)).setColor(event.getMember().getColor()).queue(event.getChannel());
                } else {
                    new MessageBuilder("The music player is not playing anything!").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            } else if(message[1].equalsIgnoreCase("list") || message[1].equalsIgnoreCase("queue") || message[1].equalsIgnoreCase("q")) {
                Queue<AudioTrack> queue = scheduler.queue;
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        new MessageBuilder("The queue is currently empty!").setColor(event.getMember().getColor()).queue(event.getChannel());
                    } else {
                        int trackCount = 0;
                        long queueLength = 0;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Current Queue Entries: ").append(queue.size()).append("\n\n");
                        for (AudioTrack track : queue) {
                            queueLength += track.getDuration();
                            if (trackCount < 10) {
                                sb.append("`[").append(getTimestamp(track.getDuration())).append("]` ");
                                sb.append(track.getInfo().title).append("\n");
                                trackCount++;
                            }
                        }
                        sb.append("\n").append("Total Queue Time Length: ").append(getTimestamp(queueLength));
                        new MessageBuilder(sb.toString().trim()).setColor(event.getMember().getColor()).queue(event.getChannel());
                    }
                }
            } else if(message[1].equalsIgnoreCase("shuffle")) {
                if (scheduler.queue.isEmpty()) {
                    new MessageBuilder("The queue is currently empty!").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                scheduler.shuffle();
                new MessageBuilder("The queue has been shuffled!").setColor(event.getMember().getColor()).queue(event.getChannel());
            } else {
                sendHelp(event.getGuild().getId(), rixaGuild.getGuildSettings().getPrefix(), event.getAuthor(), event.getChannel());
            }
        } else if (message.length == 3) {
            if(message[1].equalsIgnoreCase("join")) {
                VoiceChannel chan = null;
                String channelName = message[2];//getMessage(message, 2).trim();
                if (guild.getVoiceChannelsByName(channelName, true).size() >= 1) {
                    chan = guild.getVoiceChannelsByName(channelName, true).get(0);
                } else {
                    for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                        if (voiceChannel.getName().contains(channelName) || voiceChannel.getName().equalsIgnoreCase(channelName) ||
                                voiceChannel.getId().equalsIgnoreCase(channelName)) {
                            chan = voiceChannel;
                            break;
                        }
                    }
                }
                if (chan == null) {
                    new MessageBuilder("Sorry I was unable to find the VoiceChannel: `" + channelName + "`.").setColor(event.getMember().getColor()).queue(event.getChannel());
                } else {
                    synchronized (musicManagers) {
                        player.destroy();
                        guild.getAudioManager().setSendingHandler(null);
                        musicManagers.remove(guild.getId());
                    }
                    mng = getMusicManager(guild);
                    guild.getAudioManager().setSendingHandler(mng.sendHandler);
                    try {
                        new MessageBuilder("Entering Voice Channel: " + chan.getName()).setColor(event.getMember().getColor()).queue(event.getChannel());
                        guild.getAudioManager().openAudioConnection(chan);
                    } catch (PermissionException e) {
                        if (e.getPermission() == Permission.VOICE_CONNECT) {
                            new MessageBuilder("I do not have permission to join the requested voice channel.").setColor(event.getMember().getColor()).queue(event.getChannel());
                        }
                    }
                }
            } else if(message[1].equalsIgnoreCase("play") || message[1].equalsIgnoreCase("playlist") || message[1].equalsIgnoreCase("pplay")) {
                if (event.getMember().getVoiceState().getChannel() == null) {
                    new MessageBuilder("You must be in a voice channel to summon me!").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                try {
                    guild.getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
                    loadAndPlay(mng, event.getChannel(), message[2], (message[2].toLowerCase().contains("playlist")
                            || message[1].equalsIgnoreCase("playlist")
                            || message[1].equalsIgnoreCase("pplay")));
                } catch (PermissionException e) {
                    if (e.getPermission() == Permission.VOICE_CONNECT) {
                        new MessageBuilder("I do not have permission to join the requested voice channel.").setColor(event.getMember().getColor()).queue(event.getChannel());
                    }
                }
            } else  if(message[1].equalsIgnoreCase("vol") || message[1].equalsIgnoreCase("volume")) {
                try {
                    int newVolume = Math.max(10, Math.min(100, Integer.parseInt(message[2])));
                    int oldVolume = player.getVolume();
                    player.setVolume(newVolume);
                    new MessageBuilder("Music player volume changed from " + oldVolume + " to " + newVolume).setColor(event.getMember().getColor()).queue(event.getChannel());
                } catch (NumberFormatException e) {
                    new MessageBuilder(message[2] + " is not a valid integer. Try a number between 10 and 100.").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            } else {
                sendHelp(event.getGuild().getId(), rixaGuild.getGuildSettings().getPrefix(), event.getAuthor(), event.getChannel());
            }
        } // music youtube <query
        else if(message.length >= 3) {
            if(message[1].equalsIgnoreCase("join")) {
                VoiceChannel chan = null;
                String channelName = getMessage(message, 2);
                if (guild.getVoiceChannelsByName(channelName, true).size() >= 1) {
                    chan = guild.getVoiceChannelsByName(channelName, true).get(0);
                } else {
                    for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                        if (voiceChannel.getName().contains(channelName) || voiceChannel.getId().equalsIgnoreCase(channelName)
                            || voiceChannel.getName().equalsIgnoreCase(channelName)) {
                            chan = voiceChannel;
                            break;
                        }
                    }
                }
                if (chan == null) {
                    new MessageBuilder("Sorry I was unable to find the VoiceChannel: `" + message[2] + "`.").setColor(event.getMember().getColor()).queue(event.getChannel());
                } else {
                    synchronized (musicManagers) {
                        player.destroy();
                        guild.getAudioManager().setSendingHandler(null);
                        musicManagers.remove(guild.getId());
                    }
                    mng = getMusicManager(guild);
                    guild.getAudioManager().setSendingHandler(mng.sendHandler);
                    try {
                        new MessageBuilder("Entering Voice Channel: " + chan.getName()).setColor(event.getMember().getColor()).queue(event.getChannel());
                        guild.getAudioManager().openAudioConnection(chan);
                    } catch (PermissionException e) {
                        if (e.getPermission() == Permission.VOICE_CONNECT) {
                            new MessageBuilder("I do not have permission to join the requested voice channel.").setColor(event.getMember().getColor()).queue(event.getChannel());
                        }
                    }
                }
            } else if(message[1].equalsIgnoreCase("youtube") || message[1].equalsIgnoreCase("yt") || message[1].equalsIgnoreCase("search")
                    || message[1].equalsIgnoreCase("ytsearch")) {
                String search = getMessage(message, 2);
                try {
                    YoutubeSearch ytSearch = new YoutubeSearch(search);
                    loadAndPlay(mng, event.getChannel(), ytSearch.getUrl(0), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                sendHelp(event.getGuild().getId(), rixaGuild.getGuildSettings().getPrefix(), event.getAuthor(), event.getChannel());
            }
        } else {
            sendHelp(event.getGuild().getId(), rixaGuild.getGuildSettings().getPrefix(), event.getAuthor(), event.getChannel());
        }
    }

    private void loadAndPlay(MusicManager mng, final TextChannel channel, final String trackUrl, final boolean addPlaylist) {
        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String msg = "Adding to queue: " + track.getInfo().title;
                mng.scheduler.queue(track);
                new MessageBuilder(msg).setColor(Color.decode("#4CC276")).queue(channel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (addPlaylist) {
                    new MessageBuilder("Adding (" + playlist.getTracks().size() +") tracks to queue from playlist: " + playlist.getName()).setColor(Color.decode("#4CC276")).queue(channel);
                    tracks.forEach(mng.scheduler::queue);
                } else {
                    new MessageBuilder("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").setColor(Color.decode("#4CC276")).queue(channel);
                    mng.scheduler.queue(firstTrack);
                }
            }

            @Override
            public void noMatches() {
                new MessageBuilder("Nothing found by " + trackUrl).setColor(Color.decode("#4CC276")).queue(channel);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                new MessageBuilder("Could not play: " + exception.getMessage()).setColor(Color.decode("#4CC276")).queue(channel);
            }
        });
    }

    private MusicManager getMusicManager(Guild guild) {
        String guildId = guild.getId();
        MusicManager mng = musicManagers.get(guildId);
        if (mng == null)
        {
            synchronized (musicManagers)
            {
                mng = musicManagers.get(guildId);
                if (mng == null)
                {
                    mng = new MusicManager(playerManager);
                    mng.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }
        return mng;
    }

    private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    private String getMessage(String[] messages, int argToBegin) {
        StringBuilder builder = new StringBuilder();
        for(int i = argToBegin; i < messages.length; i++) {
            builder.append(messages[i]).append(" ");
        }
        return builder.toString().trim();
    }

    private void sendHelp(String title, String prefix, User user, TextChannel textChannel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String stringBuilder = "\u2753" +
                " **Music Commands Help**" +
                "\n" +
                "Click a number below for information about other commands.";
        embedBuilder.setTitle(String.format("Help: %s", title));
        embedBuilder.setDescription(stringBuilder);
        embedBuilder.addField(prefix + "music join [name]", "Joins a voice channel that has the provided name", false)
                .addField(prefix + "music join [id]", "Joins a voice channel based on the provided id.", false)
                .addField(prefix + "music leave", "Leaves the voice channel that the bot is currently in.", false)
                .addField(prefix + "music play", "Plays songs from the current queue. Starts playing again if it was previously paused", false)
                .addField(prefix + "music play [url]", "Adds a new song to the queue and starts playing if it wasn't playing already", false)
                .addField(prefix + "music playlist", "Adds a playlist to the queue and starts playing if not already playing", false)
                .addField(prefix + "music pause", "Pauses audio playback", false)
                .addField(prefix + "music stop", "Completely stops audio playback, skipping the current song.", false)
                .addField(prefix + "music skip", "Skips the current song, automatically starting the next", false)
                .addField(prefix + "music nowplaying", "Prints information about the currently playing song (title, current time)", false)
                .addField(prefix + "music np", "Alias for nowplaying", false)
                .addField(prefix + "music list", "Lists the songs in the queue", false)
                .addField(prefix + "music volume [vol]", "Sets the volume of the MusicPlayer [10 - 100]", false)
                .addField(prefix + "music restart", "Restarts the current song or restarts the previous song if there is no current song playing.", false)
                .addField(prefix + "music repeat", "Makes the player repeat the currently playing song", false)
                .addField(prefix + "music reset", "Completely resets the player, fixing all errors and clearing the queue.", false)
                .addField(prefix + "music shuffle", "Shuffle current music queue.", false);
        user.openPrivateChannel().complete().sendMessage(embedBuilder.build()).queue();
        new MessageBuilder(user.getAsMention() + ", help menu delivered in private messages.").setColor(Color.decode("#4CC276")).queue(textChannel);
    }
}
