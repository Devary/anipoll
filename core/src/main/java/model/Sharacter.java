package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sharacter")
@NamedQuery(
        name = "Sharacter.updateAnimeById",
        query = "update Sharacter c set c.anime = :anime where c.id = :id"
)
public class Sharacter extends BaseEntity {
    //@ManyToOne
    //@JoinColumn(name = "anime_id")
    //private Anime anime;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH)
    @JoinColumn(name = "anime_id")
    private Anime anime;
}
