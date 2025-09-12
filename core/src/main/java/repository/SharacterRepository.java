package repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import model.Sharacter;

@ApplicationScoped
public class SharacterRepository implements PanacheRepository<Sharacter> {
}
