package model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "voting_line")
public class VotingLine extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = jakarta.persistence.CascadeType.DETACH)
    @JoinColumn(name = "sharacter_id")
    private Sharacter sharacter;

    @ManyToOne(fetch = FetchType.EAGER)
    private Poll poll;

    public VotingLine() {
    }

    public VotingLine(long id, String name, String description, String image, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt,
                      Sharacter sharacter, Poll poll) {
        super(id, name, description, image, createdAt, updatedAt);
        this.sharacter = sharacter;
        this.poll = poll;
    }

    public Sharacter getSharacter() {
        return sharacter;
    }

    public void setSharacter(Sharacter sharacter) {
        this.sharacter = sharacter;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}
