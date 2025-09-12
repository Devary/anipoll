package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
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
}
