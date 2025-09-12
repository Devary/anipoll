package repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.Anime;
import model.VotingLine;

@ApplicationScoped
public class VotingLineRepository implements PanacheRepository<VotingLine> {
}
