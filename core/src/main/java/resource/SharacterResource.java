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

import dto.SharacterDto;
import mapper.DtoMapper;
import model.Sharacter;
import service.SharacterService;

import io.smallrye.mutiny.Uni;

@Path("/sharacters")
@Produces(MediaType.APPLICATION_JSON)
public class SharacterResource {
    private final SharacterService sharacterService;

    @Inject
    public SharacterResource(SharacterService sharacterService) {
        this.sharacterService = sharacterService;
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
    public Uni<SharacterDto> get(@PathParam("id") int id) {
        return sharacterService.findById(id)
                .onItem().transform(DtoMapper::toSharacterDto);
    }

    @POST
    public Uni<SharacterDto> create(Sharacter sharacter) {
        return sharacterService.save(sharacter)
                .onItem().transform(DtoMapper::toSharacterDto);
    }

    @PUT
    @Path("/{id}")
    public Uni<SharacterDto> update(@PathParam("id") int id, Sharacter sharacter) {
        sharacter.setId(id);
        return sharacterService.save(sharacter)
                .onItem().transform(DtoMapper::toSharacterDto);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") int id) {
        return sharacterService.delete(id);
    }
}
