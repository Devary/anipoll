package resource;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import dto.AnimeDto;
import mapper.DtoMapper;
import model.Anime;
import service.AnimeService;

import io.smallrye.mutiny.Uni;

@Path("/animes")
@Produces(MediaType.APPLICATION_JSON)
public class AnimeResource {
    private final AnimeService animeService;

    @Inject
    public AnimeResource(AnimeService animeService) {
        this.animeService = animeService;
    }

    @GET
    public Uni<List<AnimeDto>> all() {
        return animeService.findAll()
                .onItem().transform(animes -> animes.stream()
                        .map(DtoMapper::toAnimeDto)
                        .collect(Collectors.toList()));
    }

    @GET
    @Path("/{id}")
    public Uni<AnimeDto> get(@PathParam("id") int id) {
        return animeService.findById(id)
                .onItem().transform(DtoMapper::toAnimeDto);
    }

    @POST
    public Uni<AnimeDto> create(Anime anime) {
        return animeService.save(anime)
                .onItem().transform(DtoMapper::toAnimeDto);
    }

    @PUT
    @Path("/{id}")
    public Uni<AnimeDto> update(@PathParam("id") int id, Anime anime) {
        anime.setId(id);
        return animeService.save(anime)
                .onItem().transform(DtoMapper::toAnimeDto);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") int id) {
        return animeService.delete(id);
    }
}
