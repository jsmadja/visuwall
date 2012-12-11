package com.visuwall.web;

import com.visuwall.domain.Builds;
import com.visuwall.domain.Wall;
import com.visuwall.domain.Walls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/builds")
public class BuildsResource {

    private static final Logger LOG = LoggerFactory.getLogger(BuildsResource.class);
    public static final String WALL_ID = "wall";

    @GET
    @Produces("application/json")
    public Response builds() {
        Wall wall = Walls.get(WALL_ID);
        if(wall == null) {
            return status(NOT_FOUND).build();
        }
        Builds builds = wall.getBuilds();
        LOG.debug("new builds request from client for "+WALL_ID+" wall ("+builds.count()+" builds)");
        return ok().entity(builds.all()).build();
    }

}

