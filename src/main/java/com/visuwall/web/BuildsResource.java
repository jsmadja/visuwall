package com.visuwall.web;

import com.visuwall.domain.Wall;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static java.util.Collections.sort;
import static javax.ws.rs.core.Response.ok;

@Path("/builds")
public class BuildsResource {

    private Wall wall = new Wall();

    @GET
    @Produces("application/json")
    public Response builds() {
        return ok().entity(wall.getBuilds().all()).build();
    }

}

