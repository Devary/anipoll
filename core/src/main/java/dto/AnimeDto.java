package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AnimeDto {
    private long id;
    private String name;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate release;
    private List<SharacterDto> sharacters;

    public AnimeDto() {
    }

    public AnimeDto(long id, String name, String description, String image, LocalDateTime createdAt, LocalDateTime updatedAt,
                    LocalDate release, List<SharacterDto> sharacters) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.release = release;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public LocalDate getRelease() {
        return release;
    }

    public void setRelease(LocalDate release) {
        this.release = release;
    }

    public List<SharacterDto> getSharacters() {
        return sharacters;
    }

    public void setSharacters(List<SharacterDto> sharacters) {
        this.sharacters = sharacters;
    }
}
