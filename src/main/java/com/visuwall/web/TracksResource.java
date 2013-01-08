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
import com.visuwall.domain.tracks.Track;
import com.visuwall.domain.tracks.Tracks;
import com.visuwall.domain.walls.Wall;
import com.visuwall.domain.walls.Walls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/walls/{wallName}/tracks")
@Produces("application/json")
public class TracksResource {

    private static final Logger LOG = LoggerFactory.getLogger(TracksResource.class);

    @PathParam("wallName")
    private String wallName;

    @GET
    public Response tracks() {
        Wall wall = Walls.get(wallName);
        Tracks tracks = wall.getTracks();
        LOG.debug("new tracks request from client for " + wall.getName() + " wall (" + tracks.count() + " tracks)");
        List<Track> allTracks = tracks.all();
        return ok().entity(allTracks).build();
    }

    @GET
    @Path("/{name}")
    public Response getTrack(@PathParam("name") String name) {
        Wall wall = Walls.get("wall");
        LOG.debug("new track request from client for " + wall.getName() + " wall (" + name + " track)");
        try {
            Track track = wall.getTrack(name);
            return ok().entity(track).build();
        } catch (RefreshableNotFoundException e) {
            return Response.status(NOT_FOUND).build();
        }
    }

}

