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
@Table(name = "poll")
@NamedQuery(
        name = "Poll.safeDelete",
        query = "DELETE Poll c WHERE c.id = :id"
)
public class Poll extends BaseEntity {
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Sharacter> sharacters = new ArrayList<>();
    @OneToMany(fetch = FetchType.EAGER)
    private List<VotingLine> voting_lines = new ArrayList<>();
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private List<Poll> subPolls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Poll parent;

}
