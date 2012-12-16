package com.visuwall.web;

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

import java.util.Set;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/walls/builds")
@Produces("application/json")
public class BuildsResource {

    private static final Logger LOG = LoggerFactory.getLogger(BuildsResource.class);
    private static final String WALL_ID = "wall";

    @GET
    @Path("/{name}")
    public Response builds(@PathParam("name") String name) {
        Wall wall = Walls.get(WALL_ID);
        if(wall == null) {
            return status(NOT_FOUND).build();
        }
        Builds builds = wall.getBuilds();
        if(!builds.contains(name)) {
            return status(NOT_FOUND).build();
        }
        LOG.debug("new build request from client for build "+name);
        return ok().entity(builds.get(name)).build();
    }

    @GET
    public Response builds() {
        Wall wall = Walls.get(WALL_ID);
        if(wall == null) {
            return status(NOT_FOUND).build();
        }
        Builds builds = wall.getBuilds();
        LOG.debug("new builds request from client for "+WALL_ID+" wall ("+builds.count()+" builds)");
        Set<Build> allBuilds = builds.all();
        return ok().entity(allBuilds).build();
    }


    @GET
    @Path("/{name}")
    public Response getBuild(@PathParam("name") String name) {
        Wall wall = Walls.get("wall");
        Build build = wall.getBuild(name);
        return ok().entity(build).build();
    }

    @GET
    @Path("/{name}/last")
    public Response refreshBuild(@PathParam("name") String name) {
        Wall wall = Walls.get("wall");
        Build build = wall.getBuild(name);
        build.refresh();
        return ok().entity(build).build();
    }

}

