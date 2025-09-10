package service;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
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
    public Uni<Sharacter> findById(int id) {
        return repository.findById(id);
    }

    @WithTransaction
    public Uni<Sharacter> save(Sharacter sharacter) {
        if (sharacter.getId() == null) {
            sharacter.setCreatedAt(LocalDateTime.now());
        }
        sharacter.setUpdatedAt(LocalDateTime.now());
        return repository.persist(sharacter).replaceWith(sharacter);
    }

    @WithTransaction
    public Uni<Void> delete(int id) {
        return repository.deleteById(id).replaceWithVoid();
    }
}
