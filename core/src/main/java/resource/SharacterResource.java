package resource;

import java.util.List;
import java.util.stream.Collectors;

import io.smallrye.common.annotation.Blocking;
import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import dto.SharacterDto;
import mapper.DtoMapper;
import model.Anime;
import model.Sharacter;
import service.AnimeService;
import service.SharacterService;

import io.smallrye.mutiny.Uni;

@Path("/sharacters")
@Produces(MediaType.APPLICATION_JSON)
public class SharacterResource {
    private final SharacterService sharacterService;
    private final AnimeService animeService;
    @Inject
    Vertx vertx;
    @Inject
    public SharacterResource(SharacterService sharacterService, AnimeService animeService) {
        this.sharacterService = sharacterService;
        this.animeService = animeService;
    }

    @GET
    public Uni<List<SharacterDto>> all() {
        return sharacterService.findAll()
                .onItem().transform(sharacters -> sharacters.stream()
                        .map(DtoMapper::toSharacterDto)
                        .collect(Collectors.toList()));
    }

    @GET
    @Path("/{id}")
    public Uni<SharacterDto> get(@PathParam("id") long id) {
        return sharacterService.findById(id)
                .onItem().transform(DtoMapper::toSharacterDto);
    }

    @POST
    public Uni<SharacterDto> create(Sharacter sharacter) {
        return sharacterService.save(sharacter).onItem().transform(DtoMapper::toSharacterDto);
    }

    @PUT
    @Path("/{id}")
    public Uni<SharacterDto> update(@PathParam("id") long id, Sharacter sharacter) {
        return sharacterService.save(sharacter).onItem().transform(DtoMapper::toSharacterDto);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") int id) {
        return sharacterService.delete(id);
    }
}
