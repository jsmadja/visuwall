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
