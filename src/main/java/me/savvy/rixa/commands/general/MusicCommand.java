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
import me.savvy.rixa.guild.RixaManager;
import me.savvy.rixa.modules.music.MusicManager;
import me.savvy.rixa.modules.music.TrackScheduler;
import me.savvy.rixa.utils.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

/**
 * Created by Timber on 6/6/2017.
 */
public class MusicCommand implements CommandExec {

    public final int DEFAULT_VOLUME = 35; //(0 - 150, where 100 is default max volume)
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

    @Command(aliases = {},
            description = "Play music in your voice chat.",
            type = CommandType.USER,
            channelType = ChannelType.TEXT,
            usage = "%pmusic", mainCommand = "music")
    public void execute(GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        RixaGuild rixaGuild = RixaManager.getGuild(guild);
        if(!rixaGuild.getMusicModule().isEnabled()) {
            new MessageBuilder("Sorry music is not enabled on `" + guild.getName() + "`!").setColor(event.getMember().getColor()).queue(event.getChannel());
            return;
        }
        if(rixaGuild.getMusicModule().isRoleRequired()) {
            Role role = event.getGuild().getRoleById(rixaGuild.getMusicModule().getMusicRole());
            if(!event.getMember().getRoles().contains(role)) {
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
            sendHelp();
        } else if (message.length == 2) {
            if(message[1].equalsIgnoreCase("join")) {
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
            } else if(message[1].equalsIgnoreCase("play")) {
                if (player.isPaused()) {
                    player.setPaused(false);
                    new MessageBuilder("MusicPlayer resumed track: " + player.getPlayingTrack().getInfo().title).setColor(event.getMember().getColor()).queue(event.getChannel());
                } else if (player.getPlayingTrack() != null) {
                    new MessageBuilder("MusicPlayer already playing track: " + player.getPlayingTrack().getInfo().title).setColor(event.getMember().getColor()).queue(event.getChannel());
                } else if (scheduler.queue.isEmpty()) {
                    new MessageBuilder("The audio queue is empty! Add a track to the queue first!").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            } else if(message[1].equalsIgnoreCase("skip")) {
                AudioTrack track = scheduler.nextTrack();
                if(track != null) {
                    new MessageBuilder("The current track has been skipped. Now Playing: " + track.getInfo().title).setColor(event.getMember().getColor()).queue(event.getChannel());
                } else {
                    new MessageBuilder("Track Skipped. Queue Complete").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
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
                    event.getChannel().sendMessage("Restarting track: " + track.getInfo().title).queue();
                    player.playTrack(track.makeClone());
                } else {
                    new MessageBuilder("No track has been previously played.").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            } else if(message[1].equalsIgnoreCase("repeat")) {
                scheduler.setRepeating(!scheduler.isRepeating());
                new MessageBuilder("Repeat on music play has been " + (scheduler.isRepeating() ? "enabled" : "disabled")).setColor(event.getMember().getColor()).queue(event.getChannel());
            } else if(message[1].equalsIgnoreCase("restart")) {
                synchronized (musicManagers) {
                    scheduler.queue.clear();
                    player.destroy();
                    guild.getAudioManager().setSendingHandler(null);
                    musicManagers.remove(guild.getId());
                }

                mng = getMusicManager(guild);
                guild.getAudioManager().setSendingHandler(mng.sendHandler);
                new MessageBuilder("The music player has been reset!").setColor(event.getMember().getColor()).queue(event.getChannel());
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
            } else if(message[1].equalsIgnoreCase("list")) {
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
                        event.getChannel().sendMessage(sb.toString()).queue();
                    }
                }
            } else if(message[1].equalsIgnoreCase("shuffle")) {
                if (scheduler.queue.isEmpty()) {
                    new MessageBuilder("The queue is currently empty!").setColor(event.getMember().getColor()).queue(event.getChannel());
                    return;
                }
                scheduler.shuffle();
                new MessageBuilder("The queue has been shuffled!").setColor(event.getMember().getColor()).queue(event.getChannel());
            }
        } else if (message.length == 3) {
            if(message[1].equalsIgnoreCase("join")) {
                VoiceChannel chan = null;
                if (guild.getVoiceChannelsByName(message[2], true).size() >= 1) {
                    chan = guild.getVoiceChannelsByName(message[2], true).get(0);
                } else {
                    for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                        if (voiceChannel.getName().contains(message[2]) || voiceChannel.getId().equalsIgnoreCase(message[2])) {
                            chan = voiceChannel;
                            break;
                        }
                    }
                }
                if (chan == null) {
                    new MessageBuilder("Sorry I was unable to find the VoiceChannel: `" + message[2] + "`.").setColor(event.getMember().getColor()).queue(event.getChannel());
                } else {
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
            } else if(message[1].equalsIgnoreCase("play") || message[1].equalsIgnoreCase("pplay")) {
                loadAndPlay(mng, event.getChannel(), message[2], false);
            } else  if(message[1].equalsIgnoreCase("vol") || message[1].equalsIgnoreCase("volume")) {
                try {
                    int newVolume = Math.max(10, Math.min(100, Integer.parseInt(message[2])));
                    int oldVolume = player.getVolume();
                    player.setVolume(newVolume);
                    new MessageBuilder("Music player volume changed from " + oldVolume + " to " + newVolume).setColor(event.getMember().getColor()).queue(event.getChannel());
                } catch (NumberFormatException e) {
                    new MessageBuilder(message[2] + " is not a valid integer. Try a number between 10 and 100.").setColor(event.getMember().getColor()).queue(event.getChannel());
                }
            }
        }
    }

    private void loadAndPlay(MusicManager mng, final TextChannel channel, final String trackUrl, final boolean addPlaylist) {
        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String msg = "Adding to queue: " + track.getInfo().title;
                mng.scheduler.queue(track);
                new MessageBuilder(msg).setColor(Color.decode("#4CC276")).queue(channel);
                channel.sendMessage(msg).queue();
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

    private void sendHelp() {
    }
}
