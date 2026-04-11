package dto;

import java.time.LocalDateTime;

import model.Sharacter;

public class VotingLineDto {
    private long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Sharacter sharacter;

    public VotingLineDto() {
    }

    public VotingLineDto(long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt,
                         Sharacter sharacter) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sharacter = sharacter;
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

    public Sharacter getSharacter() {
        return sharacter;
    }

    public void setSharacter(Sharacter sharacter) {
        this.sharacter = sharacter;
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
        private Sharacter sharacter;

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

        public Builder sharacter(Sharacter sharacter) {
            this.sharacter = sharacter;
            return this;
        }

        public VotingLineDto build() {
            return new VotingLineDto(id, name, description, createdAt, updatedAt, sharacter);
        }
    }
}
