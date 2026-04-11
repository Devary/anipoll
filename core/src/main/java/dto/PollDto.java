package dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Poll;
import model.Sharacter;
import model.VotingLine;

public class PollDto {
    private long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Poll> subPolls = new ArrayList<>();
    private List<VotingLine> voting_lines = new ArrayList<>();
    private List<Sharacter> sharacters;

    public PollDto() {
    }

    public PollDto(long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt,
                   List<Poll> subPolls, List<VotingLine> voting_lines, List<Sharacter> sharacters) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.subPolls = subPolls;
        this.voting_lines = voting_lines;
        this.sharacters = sharacters;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Poll> getSubPolls() {
        return subPolls;
    }

    public void setSubPolls(List<Poll> subPolls) {
        this.subPolls = subPolls;
    }

    public List<VotingLine> getVoting_lines() {
        return voting_lines;
    }

    public void setVoting_lines(List<VotingLine> voting_lines) {
        this.voting_lines = voting_lines;
    }

    public List<Sharacter> getSharacters() {
        return sharacters;
    }

    public void setSharacters(List<Sharacter> sharacters) {
        this.sharacters = sharacters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long id;
        private String name;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Poll> subPolls = new ArrayList<>();
        private List<VotingLine> voting_lines = new ArrayList<>();
        private List<Sharacter> sharacters;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder subPolls(List<Poll> subPolls) {
            this.subPolls = subPolls;
            return this;
        }

        public Builder voting_lines(List<VotingLine> voting_lines) {
            this.voting_lines = voting_lines;
            return this;
        }

        public Builder sharacters(List<Sharacter> sharacters) {
            this.sharacters = sharacters;
            return this;
        }

        public PollDto build() {
            return new PollDto(id, name, description, createdAt, updatedAt, subPolls, voting_lines, sharacters);
        }
    }
}
