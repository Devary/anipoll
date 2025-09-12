package service;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Poll;
import repository.AnimeRepository;
import repository.PollRepository;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PollService {
    private final PollRepository repository;

    @Inject
    public PollService(PollRepository repository) {
        this.repository = repository;
    }

    @WithSession
    public Uni<List<Poll>> findAll() {
        return repository.listAll();
    }

    @WithSession
    public Uni<Poll> findById(long id) {
        return repository.findById(id);
    }

    @WithTransaction
    public Uni<Poll> save(Poll poll) {
        if (poll.getId() == 0L) {
            poll.setCreatedAt(LocalDateTime.now());
        }
        poll.setUpdatedAt(LocalDateTime.now());
        return repository.persist(poll).replaceWith(poll);
    }

    @WithTransaction
    public Uni<Void> delete(long id) {
        return Poll.delete("#Poll.safeDelete", Parameters.with("id", id)).replaceWithVoid();
    }
}
