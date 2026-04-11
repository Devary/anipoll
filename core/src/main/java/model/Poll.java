package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "poll")
@NamedQuery(
        name = "Poll.safeDelete",
        query = "DELETE Poll c WHERE c.id = :id"
)
public class Poll extends BaseEntity {
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<Sharacter> sharacters = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    private List<VotingLine> voting_lines = new ArrayList<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Poll> subPolls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Poll parent;

    public Poll() {
    }

    public Poll(long id, String name, String description, String image, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt,
                LocalDate startDate, LocalDate endDate, List<Sharacter> sharacters, List<VotingLine> voting_lines,
                List<Poll> subPolls, Poll parent) {
        super(id, name, description, image, createdAt, updatedAt);
        this.startDate = startDate;
        this.endDate = endDate;
        this.sharacters = sharacters;
        this.voting_lines = voting_lines;
        this.subPolls = subPolls;
        this.parent = parent;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Sharacter> getSharacters() {
        return sharacters;
    }

    public void setSharacters(List<Sharacter> sharacters) {
        this.sharacters = sharacters;
    }

    public List<VotingLine> getVoting_lines() {
        return voting_lines;
    }

    public void setVoting_lines(List<VotingLine> voting_lines) {
        this.voting_lines = voting_lines;
    }

    public List<Poll> getSubPolls() {
        return subPolls;
    }

    public void setSubPolls(List<Poll> subPolls) {
        this.subPolls = subPolls;
    }

    public Poll getParent() {
        return parent;
    }

    public void setParent(Poll parent) {
        this.parent = parent;
    }
}
