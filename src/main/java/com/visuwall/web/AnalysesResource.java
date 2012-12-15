package com.visuwall.web;

import com.visuwall.domain.*;
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

@Path("/walls/analyses")
@Produces("application/json")
public class AnalysesResource {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysesResource.class);
    public static final String WALL_ID = "wall";

    @GET
    public Response analyses() {
        Wall wall = Walls.get(WALL_ID);
        if(wall == null) {
            return status(NOT_FOUND).build();
        }
        Analyses analyses = wall.getAnalyses();
        LOG.debug("new analyses request from client for "+WALL_ID+" wall ("+analyses.count()+" analyses)");
        Set<Analysis> allAnalyses = analyses.all();
        return ok().entity(allAnalyses).build();
    }


    @GET
    @Path("/{name}")
    public Response getAnalysis(@PathParam("name") String name) {
        Wall wall = Walls.get("wall");
        Analysis analysis = wall.getAnalysis(name);
        return ok().entity(analysis).build();
    }

}

