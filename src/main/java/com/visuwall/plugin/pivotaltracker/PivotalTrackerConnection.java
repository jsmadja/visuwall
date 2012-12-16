package com.visuwall.plugin.pivotaltracker;

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
    public int getRemainingPointsInCurrentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException {
        Iteration currentIteration = currentIteration(projectId);
        return currentIteration.getRemainingPoints();
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
    public int getEstimatedPointsInFuture(SoftwareProjectId projectId) throws ProjectNotFoundException {
        try {
            Stories stories = client.getStoriesInNextIterations(id(projectId));
            return stories.getEstimatedPoints();
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find project with id: " + projectId, e);
        }
    }

    @Override
    public DateMidnight getEndOfCurrentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException {
        Iteration currentIteration = currentIteration(projectId);
        return new DateMidnight(currentIteration.getFinish());
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
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(project.getName());
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
