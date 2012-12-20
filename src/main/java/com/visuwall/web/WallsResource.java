package com.visuwall.web;

import com.visuwall.domain.walls.Wall;
import com.visuwall.domain.walls.Walls;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.ws.rs.core.Response.ok;

@Path("/walls")
@Produces("application/json")
@Consumes("application/json")
public class WallsResource {

    @GET
    public Response getBuilds() {
        List<Wall> walls = new ArrayList<Wall>(Walls.all());
        Collections.sort(walls);
        return ok().entity(walls).build();
    }

    @DELETE
    @Path("/{name}")
    @Consumes("*/*")
    public Response deleteWall(@PathParam("name") String name) {
        Walls.delete(name);
        return ok().build();
    }

}