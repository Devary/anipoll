package mapper;

import java.util.List;
import java.util.stream.Collectors;

import dto.AnimeDto;
import dto.PollDto;
import dto.SharacterDto;
import dto.VotingLineDto;
import model.Anime;
import model.Poll;
import model.Sharacter;
import model.VotingLine;

public class DtoMapper {
    public static AnimeDto toAnimeDto(Anime anime) {
        if (anime == null) {
            return null;
        }
        List<SharacterDto> sharacters = null;
        if (anime.getSharacters() != null) {
            sharacters = anime.getSharacters().stream()
                    .map(DtoMapper::toSharacterDtoWithoutAnime)
                    .collect(Collectors.toList());
        }
        return new AnimeDto(anime.getId(), anime.getName(), anime.getDescription(), anime.getImage(),
                anime.getCreatedAt(), anime.getUpdatedAt(), anime.getRelease(), sharacters);
    }

    public static AnimeDto toAnimeDtoWithoutSharacters(Anime anime) {
        if (anime == null) {
            return null;
        }
        return new AnimeDto(anime.getId(), anime.getName(), anime.getDescription(), anime.getImage(),
                anime.getCreatedAt(), anime.getUpdatedAt(), anime.getRelease(), null);
    }

    public static SharacterDto toSharacterDto(Sharacter sharacter) {
        if (sharacter == null) {
            return null;
        }
        return new SharacterDto(sharacter.getId(), sharacter.getName(), sharacter.getDescription(),
                sharacter.getImage(), sharacter.getCreatedAt(), sharacter.getUpdatedAt(),
                toAnimeDtoWithoutSharacters(sharacter.getAnime()));
    }

    public static SharacterDto toSharacterDtoWithoutAnime(Sharacter sharacter) {
        if (sharacter == null) {
            return null;
        }
        return new SharacterDto(sharacter.getId(), sharacter.getName(), sharacter.getDescription(),
                sharacter.getImage(), sharacter.getCreatedAt(), sharacter.getUpdatedAt(), null);
    }

    public static PollDto toPollDto(Poll poll) {
        if (poll == null) {
            return null;
        }
        return PollDto.builder()
                .id(poll.getId())
                .name(poll.getName())
                .description(poll.getDescription())
                .createdAt(poll.getCreatedAt())
                .updatedAt(poll.getUpdatedAt())
                .subPolls(poll.getSubPolls())
                .voting_lines(poll.getVoting_lines())
                .sharacters(poll.getSharacters())
                .build();
    }

    public static VotingLineDto toVotingLineDto(VotingLine votingLine) {
        if (votingLine == null) {
            return null;
        }
        return VotingLineDto.builder()
                .id(votingLine.getId())
                .name(votingLine.getName())
                .description(votingLine.getDescription())
                .createdAt(votingLine.getCreatedAt())
                .updatedAt(votingLine.getUpdatedAt())
                .sharacter(votingLine.getSharacter())
                .build();
    }
}
