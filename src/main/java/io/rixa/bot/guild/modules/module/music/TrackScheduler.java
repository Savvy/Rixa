package io.rixa.bot.guild.modules.module.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.rixa.bot.pagination.ObjectPagination;
import io.rixa.bot.pagination.QueuePagination;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {
    @Getter @Setter private boolean repeating = false, playlistRepeat = false;
    @Getter private final AudioPlayer player;
    @Getter private final Queue<AudioTrack> queue;
    @Getter private AudioTrack lastTrack;
    @Getter private QueuePagination queuePagination;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
        queuePagination = new QueuePagination(queue, 5);
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
        queuePagination.updateList(queue);
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public AudioTrack nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        AudioTrack track = queue.poll();
        player.startTrack(track, false);
        queuePagination.updateList(queue);
        return track;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if (repeating)
                player.startTrack(lastTrack.makeClone(), false);
            else {
                if (playlistRepeat) {
                    queue(lastTrack.makeClone());
                }
                nextTrack();
            }
        }

    }

    public void shuffle() {
        Collections.shuffle((List<?>) queue);
        queuePagination.updateList(queue);
    }
}
