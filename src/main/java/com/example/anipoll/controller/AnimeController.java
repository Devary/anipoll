package com.example.anipoll.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.anipoll.model.Anime;
import com.example.anipoll.service.AnimeService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/animes")
public class AnimeController {
    private final AnimeService animeService;

    public AnimeController(AnimeService animeService) {
        this.animeService = animeService;
    }

    @GetMapping
    public Flux<Anime> all() {
        return animeService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Anime> get(@PathVariable String id) {
        return animeService.findById(id);
    }

    @PostMapping
    public Mono<Anime> create(@RequestBody Anime anime) {
        return animeService.save(anime);
    }

    @PutMapping("/{id}")
    public Mono<Anime> update(@PathVariable String id, @RequestBody Anime anime) {
        anime.setId(id);
        return animeService.save(anime);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return animeService.delete(id);
    }
}
