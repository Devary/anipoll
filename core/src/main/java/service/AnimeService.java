package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;

import model.Anime;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class AnimeService {
    private final Map<Integer, Anime> storage = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    public Uni<List<Anime>> findAll() {
        return Uni.createFrom().item(() -> new ArrayList<>(storage.values()));
    }

    public Uni<Anime> findById(int id) {
        return Uni.createFrom().item(storage.get(id));
    }

    public Uni<Anime> save(Anime anime) {
        if (anime.getId() == -1) {
            anime.setId(counter.getAndIncrement());
            anime.setCreatedAt(LocalDateTime.now());
        }
        anime.setUpdatedAt(LocalDateTime.now());
        storage.put(anime.getId(), anime);
        return Uni.createFrom().item(anime);
    }

    public Uni<Void> delete(int id) {
        storage.remove(id);
        return Uni.createFrom().voidItem();
    }
}
