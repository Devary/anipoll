package repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.Anime;
import model.Poll;

@ApplicationScoped
public class PollRepository implements PanacheRepository<Poll> {
}
