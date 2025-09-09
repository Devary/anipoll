package com.example.anipoll.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.anipoll.model.Anime;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AnimeService {
    private final Map<String, Anime> store = new ConcurrentHashMap<>();

    public Flux<Anime> findAll() {
        return Flux.fromIterable(store.values());
    }

    public Mono<Anime> findById(String id) {
        return Mono.justOrEmpty(store.get(id));
    }

    public Mono<Anime> save(Anime anime) {
        if (anime.getId() == null) {
            anime.setId(UUID.randomUUID().toString());
            anime.setCreatedAt(LocalDateTime.now());
        }
        anime.setUpdatedAt(LocalDateTime.now());
        store.put(anime.getId(), anime);
        return Mono.just(anime);
    }

    public Mono<Void> delete(String id) {
        store.remove(id);
        return Mono.empty();
    }
}
