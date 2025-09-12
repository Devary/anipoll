package repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import model.Anime;

@ApplicationScoped
public class AnimeRepository implements PanacheRepository<Anime> {
}
