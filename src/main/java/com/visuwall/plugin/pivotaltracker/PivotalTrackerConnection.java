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

package com.visuwall.plugin.pivotaltracker;

import com.visuwall.api.domain.Backlog;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.TrackCapability;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.client.pivotaltracker.PivotalTrackerClient;
import com.visuwall.client.pivotaltracker.resource.Iteration;
import com.visuwall.client.pivotaltracker.resource.Project;
import com.visuwall.client.pivotaltracker.resource.Stories;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.visuwall.plugin.pivotaltracker.VisuwallApiConverter.asVisuwallStories;

public class PivotalTrackerConnection implements BasicCapability, TrackCapability {

    private PivotalTrackerClient client;
    private static final Logger LOG = LoggerFactory.getLogger(PivotalTrackerConnection.class);
    private String url;

    @Override
    public void connect(String url, String login, String password) {
        client = new PivotalTrackerClient(url, login, password);
        this.url = url;
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        try {
            for (Project project : client.getProjects()) {
                if (project.getId().toString().equals(projectId.getProjectId())) {
                    return project.getName();
                }
            }
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find project with id: " + projectId, e);
        }
        throw new ProjectNotFoundException("Cannot find project with id: " + projectId);
    }

    @Override
    public int getVelocity(SoftwareProjectId projectId) throws ProjectNotFoundException {
        try {
            Project project = client.getProject(id(projectId));
            return project.getCurrentVelocity();
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find project with id: " + projectId, e);
        }
    }

    @Override
    public com.visuwall.api.domain.Iteration getCurrentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException {
        Iteration currentIteration = currentIteration(projectId);
        DateMidnight end = new DateMidnight(currentIteration.getFinish());
        Stories stories = currentIteration.getStories();
        return new com.visuwall.api.domain.Iteration(asVisuwallStories(stories), end);
    }



    @Override
    public Backlog getBackLog(SoftwareProjectId projectId) throws ProjectNotFoundException {
        try {
            Stories stories = client.getStoriesInNextIterations(id(projectId));
            return new Backlog(asVisuwallStories(stories));
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find project with id: " + projectId, e);
        }
    }

    private Iteration currentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException {
        try {
            return client.getCurrentIteration(id(projectId));
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find project with id: " + projectId, e);
        }
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>();
        try {
            for (Project project : client.getProjects()) {
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(project.getId().toString());
                softwareProjectIds.put(softwareProjectId, project.getName());
            }
        } catch (ResourceNotFoundException e) {
            LOG.error("Cannot find projects", e);
        }
        return softwareProjectIds;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public String getUrl() {
        return url;
    }

    private Integer id(SoftwareProjectId softwareProjectId) {
        return Integer.valueOf(softwareProjectId.getProjectId());
    }

}
