package model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "sharacter")
@NamedQuery(
        name = "Sharacter.updateAnimeById",
        query = "update Sharacter c set c.anime = :anime where c.id = :id"
)
public class Sharacter extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "anime_id")
    private Anime anime;

    public Sharacter() {
    }

    public Sharacter(long id, String name, String description, String image, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt,
                     Anime anime) {
        super(id, name, description, image, createdAt, updatedAt);
        this.anime = anime;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }
}
