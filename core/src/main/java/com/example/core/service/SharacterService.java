package com.example.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.example.core.model.Anime;
import com.example.core.model.Sharacter;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class SharacterService {
    private final Map<Long, Sharacter> storage = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);
    private final AnimeService animeService;

    @Inject
    public SharacterService(AnimeService animeService) {
        this.animeService = animeService;
    }

    public Uni<List<Sharacter>> findAll() {
        return Uni.createFrom().item(() -> new ArrayList<>(storage.values()));
    }

    public Uni<Sharacter> findById(Long id) {
        return Uni.createFrom().item(storage.get(id));
    }

    public Uni<Sharacter> save(Long animeId, Sharacter sharacter) {
        return animeService.findById(animeId).onItem().transformToUni(anime -> {
            if (anime == null) {
                return Uni.createFrom().nullItem();
            }
            if (sharacter.getId() == null) {
                sharacter.setId(counter.getAndIncrement());
                sharacter.setCreatedAt(LocalDateTime.now());
            }
            sharacter.setUpdatedAt(LocalDateTime.now());
            sharacter.setAnime(anime);
            storage.put(sharacter.getId(), sharacter);
            anime.getSharacters().removeIf(s -> s.getId().equals(sharacter.getId()));
            anime.getSharacters().add(sharacter);
            return Uni.createFrom().item(sharacter);
        });
    }

    public Uni<Void> delete(Long id) {
        Sharacter removed = storage.remove(id);
        if (removed != null && removed.getAnime() != null) {
            removed.getAnime().getSharacters().removeIf(s -> s.getId().equals(id));
        }
        return Uni.createFrom().voidItem();
    }
}
