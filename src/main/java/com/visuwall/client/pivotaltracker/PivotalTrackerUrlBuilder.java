package com.visuwall.client.pivotaltracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PivotalTrackerUrlBuilder {

    private static final String API = "/services/v3";
    private String url;

    private static final Logger LOG = LoggerFactory.getLogger(PivotalTrackerUrlBuilder.class);

    public PivotalTrackerUrlBuilder(String url) {
        this.url = url;
    }

    public String getAuthenticationTokenUrl() {
        return url + API + "/tokens/active";
    }

    public String getAllProjectsUrl() {
        return url + API + "/projects";
    }

    public String getAllStoriesUrl(int projectId) {
        String allStoriesUrl = url + API + "/projects/" + projectId + "/stories";
        LOG.trace(allStoriesUrl);
        return allStoriesUrl;
    }

    public String getProjectUrl(int projectId) {
        String projectUrl = url + API + "/projects/" + projectId;
        LOG.trace(projectUrl);
        return projectUrl;
    }

    public String getBacklogUrl(int projectId) {
        String backlogUrl = url + API + "/projects/" + projectId + "/iterations/current_backlog";
        LOG.trace(backlogUrl);
        return backlogUrl;
    }

}
