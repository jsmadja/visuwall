/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

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
