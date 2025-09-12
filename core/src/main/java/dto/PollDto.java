package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Poll;
import model.Sharacter;
import model.VotingLine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDto {
    private long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Poll> subPolls = new ArrayList<>();
    private List<VotingLine> voting_lines = new ArrayList<>();
    private List<Sharacter> sharacters;
}
