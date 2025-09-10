package mapper;

import java.util.List;
import java.util.stream.Collectors;

import dto.AnimeDto;
import dto.SharacterDto;
import model.Anime;
import model.Sharacter;

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
}
