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
import com.visuwall.domain.analyses.Analyses;
import com.visuwall.domain.analyses.Analysis;
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

@Path("/walls/{wallName}/analyses")
@Produces("application/json")
public class AnalysesResource {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysesResource.class);

    @PathParam("wallName")
    private String wallName;

    @GET
    public Response analyses() {
        Wall wall = Walls.get(wallName);
        Analyses analyses = wall.getAnalyses();
        LOG.debug("new analyses request from client for " + wall.getName() + " wall (" + analyses.count() + " analyses)");
        List<Analysis> allAnalyses = analyses.all();
        return ok().entity(allAnalyses).build();
    }


    @GET
    @Path("/{name}")
    public Response getAnalysis(@PathParam("name") String name) {
        Wall wall = Walls.get(wallName);
        LOG.debug("new analysis request from client for " + wall.getName() + " wall (" + name + " analysis)");
        try {
            Analysis analysis = wall.getAnalysis(name);
            return ok().entity(analysis).build();
        } catch (RefreshableNotFoundException e) {
            return Response.status(NOT_FOUND).build();
        }
    }

}

