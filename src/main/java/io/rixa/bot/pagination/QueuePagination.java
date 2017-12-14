package io.rixa.bot.pagination;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueuePagination {

    @Getter private Queue<AudioTrack> objects;
    @Getter List<AudioTrack> listObjects;
    @Getter private int maxPage, pageSize;
    public QueuePagination(Queue<AudioTrack> objects, int pageSize) {
        this.objects = objects;
        this.pageSize = pageSize;
        this.maxPage = (objects.size() / pageSize + (objects.size() % pageSize));
    }

    public List<AudioTrack> getPage(int page) {
        if(listObjects.isEmpty()) {
            return Collections.emptyList();
        }
        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(objects.size() < fromIndex){
            return Collections.emptyList();
        }
        // toIndex exclusive
        return listObjects.subList(fromIndex, Math.min(fromIndex + pageSize, objects.size()));
    }

    public List<AudioTrack> asList() {
        return listObjects;
    }

    public void updateList(Queue<AudioTrack> obj) {
        this.objects = obj;
        this.listObjects = new LinkedList<>(getObjects());
    }
}
