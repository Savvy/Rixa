package io.rixa.bot.commands.cmds.general;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
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
import io.rixa.bot.apis.YoutubeSearch;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.MusicModule;
import io.rixa.bot.guild.modules.module.music.MusicManager;
import io.rixa.bot.pagination.Pagination;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicCommand extends Command {

    private final int DEFAULT_VOLUME = 35;
    private final AudioPlayerManager playerManager;

    public MusicCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
        this.playerManager = new DefaultAudioPlayerManager();
        this.playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        this.playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        this.playerManager.registerSourceManager(new BandcampAudioSourceManager());
        this.playerManager.registerSourceManager(new VimeoAudioSourceManager());
        this.playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        this.playerManager.registerSourceManager(new HttpAudioSourceManager());
        this.playerManager.registerSourceManager(new LocalAudioSourceManager());
        this.playerManager.registerSourceManager(new BeamAudioSourceManager());

    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        MusicModule musicModule = (MusicModule) rixaGuild.getModule("Music");
        if (musicModule.getMusicRole() != null && !member.getRoles().contains(musicModule.getMusicRole())) {
            MessageFactory.create("You do not have the required music role (" + musicModule.getMusicRole().getName() +
                    ") to use this command").setTimestamp().setColor(member.getColor()).queue(channel);
            return;
        }
        MusicManager musicManager = getMusicManager(rixaGuild);
        AudioPlayer player = musicManager.getPlayer();
        Pagination queuePagination = musicManager.getScheduler().getQueuePagination();
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "leave":
                    MessageFactory.create("Leaving voice channel...").setColor(member.getColor()).queue(channel);
                    reset(guild, musicManager);
                    channel.getGuild().getAudioManager().closeAudioConnection();
                    break;
                case "join":
                case "summon":
                    if (member.getVoiceState().getChannel() == null) {
                        MessageFactory.create("You must be in a voice channel to summon me!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    VoiceChannel voiceChannel = member.getVoiceState().getChannel();
                    joinVoice(voiceChannel, member, channel);
                    break;
                case "pause":
                case "resume":
                case "play":
                    if (player.isPaused()) {
                        player.setPaused(false);
                        MessageFactory.create("MusicPlayer resumed track: " + player.getPlayingTrack().getInfo().title).setColor(member.getColor()).queue(channel);
                    } else if (!(player.isPaused())) {
                        player.setPaused(false);
                        MessageFactory.create("MusicPlayer paused track: " + player.getPlayingTrack().getInfo().title).setColor(member.getColor()).queue(channel);
                    } else if (player.getPlayingTrack() != null) {
                        MessageFactory.create("MusicPlayer already playing track: " + player.getPlayingTrack().getInfo().title).setColor(member.getColor()).queue(channel);
                    } else if (musicManager.getScheduler().getQueue().isEmpty()) {
                        MessageFactory.create("The audio queue is empty! Add a track to the queue first!").setColor(member.getColor()).queue(channel);
                    }
                    break;
                case "stop":
                    musicManager.getScheduler().getQueue().clear();
                    player.stopTrack();
                    player.setPaused(false);
                    MessageFactory.create("The music player has been stopped and queue has been cleared.").setColor(member.getColor()).queue(channel);
                    break;
                case "skip":
                    AudioTrack audioTrack = musicManager.getScheduler().nextTrack();
                    if (audioTrack == null) {
                        MessageFactory.create("The queue is now empty, add more to continue to listen to music!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    MessageFactory.create("Successfully skipped current track. Now playing: " + audioTrack.getInfo().title).setColor(member.getColor()).queue(channel);
                    break;
                case "nowplaying":
                case "np":
                    audioTrack = musicManager.getScheduler().getPlayer().getPlayingTrack();
                    if (audioTrack == null) {
                        MessageFactory.create("The audio queue is empty! Add a track to the queue first!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    MessageFactory.create().setAuthor(audioTrack.getInfo().title, "https://i.imgur.com/AnaMjsH.png")
                            .addField("Author", audioTrack.getInfo().author, true)
                            .addField("Duration", getTimestamp(audioTrack.getInfo().length), true)
                            .addField("Position", getTimestamp(audioTrack.getPosition()), true).queue(channel);
                    break;
                case "playlist":
                case "queue":
                case "list":
                    if (musicManager.getScheduler().getQueue().size() == 0) {
                        MessageFactory.create("The audio queue is empty! Add a track to the queue first!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    List<Object> firstPage = queuePagination.getPage(1);
                    if (firstPage == null || firstPage.isEmpty()) {
                        MessageFactory.create("The audio queue is empty! Add a track to the queue first!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    List<String> titles = new ArrayList<>();
                    for (int i = 0; i < firstPage.size(); i++) {
                        if (firstPage.get(i) == null) continue;
                        titles.add("`" + (i + 1) + ")` " + ((AudioTrack) firstPage.get(i)).getInfo().title);
                    }
                    MessageFactory.create(String.join("\n", titles)).setAuthor("Music Queue", "https://i.imgur.com/AnaMjsH.png")
                            .queue(channel, message -> {
                                message.addReaction("\u2B05").queue();
                                message.addReaction("\u27A1").queue();
                            });
                    break;
                case "restart":
                    audioTrack = player.getPlayingTrack();
                    if (audioTrack == null) {
                        audioTrack = musicManager.getScheduler().getLastTrack();
                    }
                    if (audioTrack == null) {
                        MessageFactory.create("No track has been previously played.").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    MessageFactory.create("Restarting Track: " + audioTrack.getInfo().title).setColor(member.getColor()).queue(channel);
                    player.playTrack(audioTrack.makeClone());
                    break;
                case "repeat":
                    musicManager.getScheduler().setRepeating(!musicManager.getScheduler().isRepeating());
                    MessageFactory.create("Music repeat has been " + (musicManager.getScheduler().isRepeating() ? "enabled" : "disabled"))
                            .setColor(member.getColor()).queue(channel);
                    break;
                case "playlistrepeat":
                case "pr":
                    musicManager.getScheduler().setPlaylistRepeat(!musicManager.getScheduler().isPlaylistRepeat());
                    MessageFactory.create("Playlist repeat has been " + (musicManager.getScheduler().isPlaylistRepeat() ? "enabled" : "disabled"))
                            .setColor(member.getColor()).queue(channel);
                    break;
                case "reset":
                    reset(guild, musicManager);
                    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
                    MessageFactory.create("The player has been completely reset!").setColor(member.getColor()).queue(channel);
                    break;
                case "shuffle":
                    if (musicManager.getScheduler().getQueue().isEmpty()) {
                        MessageFactory.create("The queue is currently empty!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    musicManager.getScheduler().shuffle();
                    MessageFactory.create("The queue has been shuffled!").setColor(member.getColor()).queue(channel);
                    break;
            }
        } else if (args.length == 2) {
            String string = StringUtils.join(args, " ", 1, args.length);
            switch (args[0].toLowerCase()) {
                case "join":
                    VoiceChannel voiceChannel = DiscordUtils.voiceChannelSearch(guild, string);
                    if (voiceChannel == null) {
                        return;
                    }
                    joinVoice(voiceChannel, member, channel);
                    break;
                case "play":
                case "playlist":
                case "pplay":
                    if (member.getVoiceState().getChannel() == null) {
                        MessageFactory.create("You must be in a voice channel to summon me!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    if (guild.getSelfMember().getVoiceState().getChannel() == null) {
                        joinVoice(member.getVoiceState().getChannel(), member, channel);
                    }
                    loadAndPlay(musicManager, channel, args[1], (args[1].toLowerCase().contains("playlist")
                            || args[0].equalsIgnoreCase("playlist")
                            || args[0].equalsIgnoreCase("pplay")));
                    break;
                case "volume":
                case "vol":
                    try {
                        int newVolume = Math.max(10, Math.min(100, Integer.parseInt(args[1])));
                        int oldVolume = player.getVolume();
                        player.setVolume(newVolume);
                        MessageFactory.create("Music player volume updated from `" + oldVolume + "` to `" + newVolume + "`").setColor(member.getColor()).queue(channel);
                    } catch (NumberFormatException e) {
                        MessageFactory.create(args[1] + " is not a valid integer. Try a number between 10 and 100.").setColor(member.getColor()).queue(channel);
                    }
                    break;
                case "forward":
                    if (player.getPlayingTrack() == null) {
                        MessageFactory.create("The audio queue is empty! Add a track to the queue first!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    try {
                        player.getPlayingTrack().setPosition(Math.max(0, player.getPlayingTrack().getPosition() + (Integer.parseInt(args[1]) * 1000)));
                    } catch (NumberFormatException e) {
                        MessageFactory.create(args[1] + " is not a valid integer. Try `10`!").setColor(member.getColor()).queue(channel);
                    }
                    break;
                case "back":
                    if (player.getPlayingTrack() == null) {
                        MessageFactory.create("The audio queue is empty! Add a track to the queue first!").setColor(member.getColor()).queue(channel);
                        return;
                    }
                    try {
                        player.getPlayingTrack().setPosition(Math.max(0, player.getPlayingTrack().getPosition() - (Integer.parseInt(args[1]) * 1000)));
                    } catch (NumberFormatException e) {
                        MessageFactory.create(args[1] + " is not a valid integer. Try `10`!").setColor(member.getColor()).queue(channel);
                    }
                    break;
            }
        } else if (args.length >= 3) {
            String string = StringUtils.join(args, " ", 1, args.length);
            switch (args[0].toLowerCase()) {
                case "join":
                    VoiceChannel voiceChannel = DiscordUtils.voiceChannelSearch(guild, string);
                    if (voiceChannel == null) {
                        MessageFactory.create("Sorry I was unable to find the VoiceChannel: `" + string + "`.").setColor(member.getColor()).queue(channel);
                    }
                    joinVoice(voiceChannel, member, channel);
                    break;
                case "youtube":
                case "ytsearch":
                case "yt":
                case "search":
                    try {
                        YoutubeSearch ytSearch = new YoutubeSearch(string);
                        loadAndPlay(musicManager, channel, ytSearch.getUrl(0), false);
                    } catch (IOException e) {
                        MessageFactory.create("Error Occurred: Could not play youtube video.").setColor(member.getColor()).queue(channel);
                    }
                    break;
            }
        }
    }

    private void joinVoice(VoiceChannel voiceChannel, Member member, TextChannel channel) {
        try {
            channel.getGuild().getAudioManager().openAudioConnection(voiceChannel);
            MessageFactory.create("Entering Voice Channel: " + voiceChannel.getName()).setColor(member.getColor()).queue(channel);
            if (voiceChannel.getGuild().getAudioManager().getSendingHandler() == null) {
                voiceChannel.getGuild().getAudioManager().setSendingHandler
                        (getMusicManager(GuildManager.getInstance().getGuild(voiceChannel.getGuild())).getSendHandler());
            }
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                MessageFactory.create("I do not have permission to join the requested voice channel.").setColor(member.getColor()).queue(channel);
            }
        }
    }

    private void reset(Guild guild, MusicManager musicManager) {
        synchronized (musicManager) {
            musicManager.getScheduler().getQueue().clear();
            musicManager.getScheduler().getPlayer().destroy();
            guild.getAudioManager().setSendingHandler(null);
        }
    }

    private void loadAndPlay(MusicManager mng, final TextChannel channel, final String trackUrl, final boolean addPlaylist) {
        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String msg = "Adding to queue: " + track.getInfo().title;
                mng.getScheduler().queue(track);
                MessageFactory.create(msg).setColor(Color.decode("#4CC276")).queue(channel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (addPlaylist) {
                    MessageFactory.create("Adding (" + playlist.getTracks().size() + ") tracks to queue from playlist: " + playlist.getName()).setColor(Color.decode("#4CC276")).queue(channel);
                    tracks.forEach(mng.getScheduler()::queue);
                } else {
                    MessageFactory.create("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").setColor(Color.decode("#4CC276")).queue(channel);
                    mng.getScheduler().queue(firstTrack);
                }
            }

            @Override
            public void noMatches() {
                MessageFactory.create("Nothing found by " + trackUrl).setColor(Color.decode("#4CC276")).queue(channel);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                MessageFactory.create("Could not play: " + exception.getMessage()).setColor(Color.decode("#4CC276")).queue(channel);
                exception.printStackTrace();
            }
        });
    }

    public MusicManager getMusicManager(RixaGuild guild) {
        MusicModule musicModule = (MusicModule) guild.getModule("Music");
        if (musicModule.getMusicManager() != null) {
            return musicModule.getMusicManager();
        }
        MusicManager musicManager = new MusicManager(this.playerManager);
        musicManager.getPlayer().setVolume(DEFAULT_VOLUME);
        musicModule.setMusicManager(musicManager);
        return musicManager;
    }

    private String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}
