package com.visuwall.web;

import com.visuwall.domain.walls.Walls;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;

@Path("/walls/configurations")
@Produces("application/xml")
public class ConfigurationsResource {

    @GET
    public Response getConfiguration() {
        return ok().entity(Walls.all()).build();
    }

}
