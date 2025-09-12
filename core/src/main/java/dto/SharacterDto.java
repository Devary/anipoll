package dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharacterDto {
    private long id;
    private String name;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AnimeDto anime;
}
