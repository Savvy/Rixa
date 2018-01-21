package io.rixa.bot.pagination;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pagination {

    @Getter @Setter private List<Object> objects;
    @Getter private int maxPage, pageSize;

    public <E> Pagination(Collection<E> objects, int pageSize) {
        this.objects = new ArrayList<>(objects);
        this.pageSize = pageSize;
        this.maxPage = (objects.size() / pageSize + (objects.size() % pageSize));
    }

    public List<Object> getPage(int page) {
        if(objects.isEmpty()) {
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
        return objects.subList(fromIndex, Math.min(fromIndex + pageSize, objects.size()));
    }

    public <E> void updateList(Collection<E> objects) {
        this.objects.clear();
        this.objects.addAll(objects);
    }
}
