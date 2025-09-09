package com.example.core.resource;

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

import com.example.core.model.Anime;
import com.example.core.service.AnimeService;

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
    public Uni<List<Anime>> all() {
        return animeService.findAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Anime> get(@PathParam("id") Long id) {
        return animeService.findById(id);
    }

    @POST
    public Uni<Anime> create(Anime anime) {
        return animeService.save(anime);
    }

    @PUT
    @Path("/{id}")
    public Uni<Anime> update(@PathParam("id") Long id, Anime anime) {
        anime.setId(id);
        return animeService.save(anime);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> delete(@PathParam("id") Long id) {
        return animeService.delete(id);
    }
}
