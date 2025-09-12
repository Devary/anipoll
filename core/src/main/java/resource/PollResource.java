package resource;

import dto.PollDto;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import mapper.DtoMapper;
import model.Poll;
import service.PollService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/polls")
@Produces(MediaType.APPLICATION_JSON)
public class PollResource {
    private final PollService pollService;

    @Inject
    public PollResource(PollService pollService) {
        this.pollService = pollService;
    }

    @GET
    public Uni<List<PollDto>> all() {
        return pollService.findAll()
                .onItem().transform(animes -> animes.stream()
                        .map(DtoMapper::toPollDto)
                        .collect(Collectors.toList()));
    }

    @GET
    @Path("/{id}")
    public Uni<PollDto> get(@PathParam("id") int id) {
        return pollService.findById(id)
                .onItem().transform(DtoMapper::toPollDto);
    }

    @POST
    public Uni<PollDto> create(Poll poll) {
        return pollService.save(poll)
                .onItem().transform(DtoMapper::toPollDto);
    }

    @PUT
    @Path("/{id}")
    public Uni<PollDto> update(@PathParam("id") int id, Poll poll) {
        poll.setId(id);
        return pollService.save(poll)
                .onItem().transform(DtoMapper::toPollDto);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") int id) {
        return pollService.delete(id);
    }
}
