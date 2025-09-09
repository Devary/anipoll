package com.example.anipoll.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.anipoll.model.Sharacter;
import com.example.anipoll.service.SharacterService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sharacters")
public class SharacterController {
    private final SharacterService service;

    public SharacterController(SharacterService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Sharacter> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Sharacter> get(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<Sharacter> create(@RequestBody Sharacter sharacter) {
        return service.save(sharacter);
    }

    @PutMapping("/{id}")
    public Mono<Sharacter> update(@PathVariable String id, @RequestBody Sharacter sharacter) {
        sharacter.setId(id);
        return service.save(sharacter);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }
}
