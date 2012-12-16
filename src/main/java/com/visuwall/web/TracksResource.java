package com.visuwall.web;

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
import java.util.Set;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/walls/tracks")
@Produces("application/json")
public class TracksResource {

    private static final Logger LOG = LoggerFactory.getLogger(TracksResource.class);
    private static final String WALL_ID = "wall";

    @GET
    public Response tracks() {
        Wall wall = Walls.get(WALL_ID);
        if (wall == null) {
            return status(NOT_FOUND).build();
        }
        Tracks tracks = wall.getTracks();
        LOG.debug("new tracks request from client for " + WALL_ID + " wall (" + tracks.count() + " tracks)");
        Set<Track> allTracks = tracks.all();
        return ok().entity(allTracks).build();
    }

    @GET
    @Path("/{name}")
    public Response getTrack(@PathParam("name") String name) {
        LOG.debug("new track request from client for " + WALL_ID + " wall (" + name + " track)");
        Wall wall = Walls.get("wall");
        Track track = wall.getTrack(name);
        return ok().entity(track).build();
    }

}

