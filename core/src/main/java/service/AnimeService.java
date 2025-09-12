package service;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import model.Anime;
import repository.AnimeRepository;

@ApplicationScoped
public class AnimeService {
    private final AnimeRepository repository;

    @Inject
    public AnimeService(AnimeRepository repository) {
        this.repository = repository;
    }

    @WithSession
    public Uni<List<Anime>> findAll() {
        return repository.listAll();
    }

    @WithSession
    public Uni<Anime> findById(long id) {
        return repository.findById(id);
    }

    @WithTransaction
    public Uni<Anime> save(Anime anime) {
        if (anime.getId() == 0L) {
            anime.setCreatedAt(LocalDateTime.now());
        }
        anime.setUpdatedAt(LocalDateTime.now());
        return repository.persist(anime).replaceWith(anime);
    }

    @WithTransaction
    public Uni<Void> delete(long id) {
        return Anime.delete("#Anime.safeDelete", Parameters.with("id", id)).replaceWithVoid();
    }
}
