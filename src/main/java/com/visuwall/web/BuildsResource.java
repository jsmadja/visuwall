package com.visuwall.web;

import com.visuwall.domain.Builds;
import com.visuwall.domain.Wall;
import com.visuwall.domain.Walls;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/builds")
public class BuildsResource {

    @GET
    @Produces("application/json")
    public Response builds() {
        Wall wall = Walls.get("wall");
        if(wall == null) {
            return status(NOT_FOUND).build();
        }
        Builds builds = wall.getBuilds();
        return ok().entity(builds.all()).build();
    }

}

