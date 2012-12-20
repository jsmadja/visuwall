package com.visuwall.web;

import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.walls.Wall;
import com.visuwall.domain.walls.Walls;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.notModified;
import static javax.ws.rs.core.Response.ok;

@Path("/walls/{wallName}/connections")
@Produces("application/json")
@Consumes({"application/json", "text/plain"})
public class ConnectionsResource {

    @PathParam("wallName")
    private String wallName;

    @GET
    public Response getConnections() {
        Wall wall = Walls.get(wallName);
        return ok().entity(wall.getConnections()).build();
    }

    @PUT
    public Response updateConnection(Connection connection) {
        if (StringUtils.isBlank(connection.getName())) {
            return notModified().build();
        }
        Wall wall = Walls.get(wallName);
        wall.updateConnection(connection);
        return ok().entity(connection).build();
    }

    @DELETE
    @Path("/{name}")
    @Consumes("*/*")
    public Response removeConnection(@PathParam("name") String name) {
        try {
            Wall wall = Walls.get(wallName);
            wall.removeConnection(name);
            return ok().build();
        } catch (ResourceNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
