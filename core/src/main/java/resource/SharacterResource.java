package resource;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
    public Uni<List<Sharacter>> all() {
        return sharacterService.findAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Sharacter> get(@PathParam("id") Long id) {
        return sharacterService.findById(id);
    }

    @POST
    public Uni<Sharacter> create(Sharacter sharacter) {
        return sharacterService.save(sharacter);
    }

    @PUT
    @Path("/{id}")
    public Uni<Sharacter> update(@PathParam("id") Long id, Sharacter sharacter) {
        sharacter.setId(id);
        return sharacterService.save(sharacter);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") Long id) {
        return sharacterService.delete(id);
    }
}
