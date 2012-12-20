package com.visuwall.web;

import com.visuwall.domain.walls.Wall;
import com.visuwall.domain.walls.Walls;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;

@Path("/walls")
@Produces("application/json")
@Consumes("application/json")
public class WallsResource {

    @GET
    public Response getBuilds() {
        return ok().entity(Walls.all()).build();
    }

    @DELETE
    @Path("/{name}")
    @Consumes("*/*")
    public Response deleteWall(@PathParam("name") String name) {
        Walls.delete(name);
        return ok().build();
    }

}