package com.visuwall.web;

import com.visuwall.domain.walls.Walls;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

}