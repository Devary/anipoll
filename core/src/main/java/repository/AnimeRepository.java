package repository;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import model.Anime;

@ApplicationScoped
public class AnimeRepository implements PanacheRepositoryBase<Anime, Integer> {
}
