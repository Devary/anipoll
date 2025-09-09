package com.example.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;

import com.example.core.model.Anime;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AnimeService {
    private final Map<Long, Anime> storage = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public Uni<List<Anime>> findAll() {
        return Uni.createFrom().item(() -> new ArrayList<>(storage.values()));
    }

    public Uni<Anime> findById(Long id) {
        return Uni.createFrom().item(storage.get(id));
    }

    public Uni<Anime> save(Anime anime) {
        if (anime.getId() == null) {
            anime.setId(counter.getAndIncrement());
            anime.setCreatedAt(LocalDateTime.now());
        }
        anime.setUpdatedAt(LocalDateTime.now());
        storage.put(anime.getId(), anime);
        return Uni.createFrom().item(anime);
    }

    public Uni<Void> delete(Long id) {
        storage.remove(id);
        return Uni.createFrom().voidItem();
    }
}
