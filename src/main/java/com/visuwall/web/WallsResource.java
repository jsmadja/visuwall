package com.visuwall.web;

import com.visuwall.domain.Build;
import com.visuwall.domain.ConnectionConfiguration;
import com.visuwall.domain.Wall;
import com.visuwall.domain.Walls;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.net.MalformedURLException;
import java.net.URL;

import static javax.ws.rs.core.Response.ok;

@Path("/walls")
@Produces("application/json")
@Consumes("application/json")
public class WallsResource {

    @POST
    @Path("/")
    public Response addConnection(ConnectionConfiguration connectionConfiguration) throws MalformedURLException {
        URL url = new URL(connectionConfiguration.getUrl());
        Wall wall = Walls.get("wall");
        wall.addConnection(url);
        return ok().build();
    }

    @GET
    @Path("/configuration")
    public Response getConfiguration() {
        Wall wall = Walls.get("wall");
        return ok().entity(wall.getConfiguration()).build();
    }

    @GET
    @Path("/builds/{id}")
    public Response getConfiguration(@PathParam("id") buildId) {
        Wall wall = Walls.get("wall");
        Build build = wall.getBuild(buildId);
        return ok().entity(build).build();
    }

}