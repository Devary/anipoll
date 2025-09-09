package com.example.anipoll.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.anipoll.model.Anime;
import com.example.anipoll.model.Sharacter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SharacterService {
    private final Map<String, Sharacter> store = new ConcurrentHashMap<>();
    private final AnimeService animeService;

    public SharacterService(AnimeService animeService) {
        this.animeService = animeService;
    }

    public Flux<Sharacter> findAll() {
        return Flux.fromIterable(store.values());
    }

    public Mono<Sharacter> findById(String id) {
        return Mono.justOrEmpty(store.get(id));
    }

    public Mono<Sharacter> save(Sharacter sharacter) {
        if (sharacter.getId() == null) {
            sharacter.setId(UUID.randomUUID().toString());
            sharacter.setCreatedAt(LocalDateTime.now());
        }
        sharacter.setUpdatedAt(LocalDateTime.now());
        store.put(sharacter.getId(), sharacter);

        Anime anime = sharacter.getAnime();
        if (anime != null && anime.getId() != null) {
            animeService.findById(anime.getId())
                .doOnNext(a -> {
                    a.getSharacters().removeIf(s -> s.getId().equals(sharacter.getId()));
                    a.getSharacters().add(sharacter);
                })
                .flatMap(animeService::save)
                .subscribe();
        }

        return Mono.just(sharacter);
    }

    public Mono<Void> delete(String id) {
        Sharacter removed = store.remove(id);
        if (removed != null && removed.getAnime() != null && removed.getAnime().getId() != null) {
            animeService.findById(removed.getAnime().getId())
                .doOnNext(a -> a.getSharacters().removeIf(s -> s.getId().equals(id)))
                .flatMap(animeService::save)
                .subscribe();
        }
        return Mono.empty();
    }
}
