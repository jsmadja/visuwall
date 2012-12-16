package com.visuwall.client.pivotaltracker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.client.pivotaltracker.resource.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PivotalTrackerClient extends GenericSoftwareClient {

    private PivotalTrackerUrlBuilder urlBuilder;
    private String authenticationToken;

    private static Logger LOG = LoggerFactory.getLogger(PivotalTrackerClient.class);

    public PivotalTrackerClient(String url, String login, String password) {
        super(login, password);
        urlBuilder = new PivotalTrackerUrlBuilder(url);
        String authenticationTokenUrl = urlBuilder.getAuthenticationTokenUrl();
        LOG.trace(authenticationTokenUrl);
        try {
            Token token = resource(authenticationTokenUrl, Token.class);
            authenticationToken = token.getGuid();
        } catch (ResourceNotFoundException e) {
            String errorMessage = "Cannot log in pivotaltracker at " + url + " with login:'" + login + "'";
            throw new IllegalStateException(errorMessage, e);
        }
    }

    public Projects getProjects() throws ResourceNotFoundException {
        String allProjectsUrl = urlBuilder.getAllProjectsUrl();
        return (Projects) resourceWithHeaders(allProjectsUrl, Projects.class);
    }

    public Stories getStoriesOf(int projectId) throws ResourceNotFoundException {
        String allStoriesUrl = urlBuilder.getAllStoriesUrl(projectId);
        return (Stories) resourceWithHeaders(allStoriesUrl, Stories.class);
    }

    public Project getProject(int projectId) throws ResourceNotFoundException {
        String projectUrl = urlBuilder.getProjectUrl(projectId);
        Project project = (Project) resourceWithHeaders(projectUrl, Project.class);
        Stories stories = getStoriesOf(project.getId());
        project.setStories(stories);
        for (Story story : stories) {
            story.setProject(project);
        }
        return project;
    }

    private Object resourceWithHeaders(String allProjectsUrl, Class<?> clazz) throws ResourceNotFoundException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-TrackerToken", authenticationToken);
        return resource(allProjectsUrl, clazz, headers);
    }

    public Iteration getCurrentIteration(int projectId) throws ResourceNotFoundException {
        String backlogUrl = urlBuilder.getBacklogUrl(projectId);
        return ((Iterations) resourceWithHeaders(backlogUrl, Iterations.class)).get(0);
    }

    public Stories getStoriesInNextIterations(int projectId) throws ResourceNotFoundException {
        Iteration currentIteration = getCurrentIteration(projectId);
        Stories stories = getStoriesOf(projectId);
        Stories storiesInNextIterations = new Stories();
        for (Story story : stories) {
            boolean isInNextIteration = story.getCurrentState() == CurrentState.unstarted && !currentIteration.contains(story);
            if(isInNextIteration) {
                storiesInNextIterations.add(story);
            }
        }
        return storiesInNextIterations;
    }
}
