package com.visuwall.web;

import com.visuwall.domain.Wall;
import com.visuwall.domain.Walls;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;

@Path("/walls/configurations")
public class ConfigurationsResource {

    @GET
    public Response getConfiguration() {
        Wall wall = Walls.get("wall");
        return ok().entity(wall.getConfiguration()).build();
    }

}
