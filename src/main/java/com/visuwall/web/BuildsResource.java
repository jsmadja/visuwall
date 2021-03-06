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

import com.visuwall.domain.RefreshableNotFoundException;
import com.visuwall.domain.builds.Build;
import com.visuwall.domain.builds.Builds;
import com.visuwall.domain.walls.Wall;
import com.visuwall.domain.walls.Walls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.*;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/walls/{wallName}/builds")
@Produces("application/json")
public class BuildsResource {

    private static final Logger LOG = LoggerFactory.getLogger(BuildsResource.class);

    @PathParam("wallName")
    private String wallName;

    @GET
    @Path("/{name}")
    public Response builds(@PathParam("name") String name) {
        Wall wall = Walls.get(wallName);
        Builds builds = wall.getBuilds();
        if (!builds.contains(name)) {
            return status(NOT_FOUND).build();
        }
        LOG.debug("new build request from client for build " + name);
        try {
            return ok().entity(builds.get(name)).build();
        } catch (RefreshableNotFoundException e) {
            return Response.status(NOT_FOUND).build();
        }
    }

    @GET
    public Response builds() {
        Wall wall = Walls.get(wallName);
        Builds builds = wall.getBuilds();
        LOG.debug("new builds request from client for " + wall.getName() + " wall (" + builds.count() + " builds)");
        List<Build> all = new ArrayList<Build>(builds.all());
        return ok().entity(all).build();
    }


    @GET
    @Path("/{name}")
    public Response getBuild(@PathParam("name") String name) {
        Wall wall = Walls.get(wallName);
        try {
            Build build = wall.getBuild(name);
            return ok().entity(build).build();
        } catch (RefreshableNotFoundException e) {
            return Response.status(NOT_FOUND).build();
        }
    }

}

