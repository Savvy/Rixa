package io.rixa.bot.guild.modules.module.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;

public class MusicManager {
    /**
     * Audio player for the guild.
     */
    @Getter private final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    @Getter private final TrackScheduler scheduler;
    /**
     * Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    @Getter private final AudioPlayerSendHandler sendHandler;

    /**
     * Creates a player and a track scheduler.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    public MusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        sendHandler = new AudioPlayerSendHandler(player);
        player.addListener(scheduler);
    }
}
