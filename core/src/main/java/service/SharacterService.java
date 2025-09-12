package service;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.panache.common.Parameters;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.EntityManager;
import model.Anime;
import model.Sharacter;
import repository.SharacterRepository;

@ApplicationScoped
public class SharacterService {
    private final SharacterRepository repository;

    @Inject
    public SharacterService(SharacterRepository repository) {
        this.repository = repository;
    }

    @WithSession
    public Uni<List<Sharacter>> findAll() {
        return repository.listAll();
    }

    @WithSession
    public Uni<Sharacter> findById(long id) {
        return repository.findById(id);
    }

    @WithTransaction
    public Uni<Sharacter> save(Sharacter sharacter) {
        if (sharacter.getId() == 0L) {
            sharacter.setCreatedAt(LocalDateTime.now());
            return sharacter.persist().replaceWith(sharacter);
        }
        sharacter.setUpdatedAt(LocalDateTime.now());
        return Sharacter.update("#Sharacter.updateAnimeById", Parameters.with("id", sharacter.getId())
                        .and("anime", sharacter.getAnime()))
                .replaceWith(sharacter);
    }

    @WithTransaction
    public Uni<Void> delete(long id) {
        return repository.deleteById(id).replaceWithVoid();
    }
}
