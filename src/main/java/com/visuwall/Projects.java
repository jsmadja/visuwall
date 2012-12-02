package com.visuwall;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Collections.sort;
import static javax.ws.rs.core.Response.ok;

@Path("/projects")
public class Projects {

    @GET
    @Produces("application/json")
    public Response ping() {
        List<Project> projects = new Wall().allProjects();
        sort(projects);
        return ok().entity(projects).build();
    }

}

