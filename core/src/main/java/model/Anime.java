package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "anime")
@NamedQuery(
        name = "Anime.safeDelete",
        query = "DELETE Anime c WHERE c.id = :id"
)
public class Anime extends BaseEntity {
    private LocalDate release;

    @OneToMany(mappedBy = "anime", fetch = FetchType.EAGER)
    private List<Sharacter> sharacters = new ArrayList<>();

    public Anime() {
    }

    public Anime(long id, String name, String description, String image, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt,
                 LocalDate release, List<Sharacter> sharacters) {
        super(id, name, description, image, createdAt, updatedAt);
        this.release = release;
        this.sharacters = sharacters;
    }

    public LocalDate getRelease() {
        return release;
    }

    public void setRelease(LocalDate release) {
        this.release = release;
    }

    public List<Sharacter> getSharacters() {
        return sharacters;
    }

    public void setSharacters(List<Sharacter> sharacters) {
        this.sharacters = sharacters;
    }
}
