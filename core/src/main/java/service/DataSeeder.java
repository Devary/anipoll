package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.github.javafaker.Faker;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import model.Anime;
import model.Sharacter;
import repository.AnimeRepository;
import repository.SharacterRepository;

@ApplicationScoped
public class DataSeeder {

    @Inject AnimeRepository animeRepository;
    @Inject SharacterRepository sharacterRepository;

    void init(@Observes StartupEvent event) {
        if (!"dev".equals(ProfileManager.getActiveProfile())) {
            return;
        }

        Faker faker = new Faker();

        Panache.withTransaction(sharacterRepository::deleteAll).await().indefinitely();
        Panache.withTransaction(animeRepository::deleteAll).await().indefinitely();

        List<Anime> animes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Anime anime = new Anime();
            anime.setName(faker.anime().title());
            anime.setDescription(faker.lorem().sentence());
            anime.setImage(faker.internet().image());
            anime.setRelease(LocalDate.now().minusDays(faker.number().numberBetween(0, 3650)));
            anime.setCreatedAt(LocalDateTime.now());
            anime.setUpdatedAt(LocalDateTime.now());
            animes.add(anime);
        }
        Panache.withTransaction(() -> animeRepository.persist(animes)).await().indefinitely();

        Random random = new Random();
        List<Sharacter> sharacters = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Sharacter sharacter = new Sharacter();
            sharacter.setName(faker.anime().character());
            sharacter.setDescription(faker.lorem().sentence());
            sharacter.setImage(faker.internet().avatar());
            sharacter.setAnimeId(animes.get(random.nextInt(animes.size())).getId());
            sharacter.setCreatedAt(LocalDateTime.now());
            sharacter.setUpdatedAt(LocalDateTime.now());
            sharacters.add(sharacter);
        }
        Panache.withTransaction(() -> sharacterRepository.persist(sharacters)).await().indefinitely();
    }
}
