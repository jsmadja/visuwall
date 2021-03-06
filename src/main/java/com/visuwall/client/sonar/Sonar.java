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

package com.visuwall.client.sonar;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.client.sonar.domain.SonarMetrics;
import com.visuwall.client.sonar.domain.SonarQualityMetric;
import com.visuwall.client.sonar.exception.*;
import com.visuwall.client.sonar.resource.Project;
import com.visuwall.client.sonar.resource.Projects;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class Sonar {

    private GenericSoftwareClient client;

    /**
     * http://docs.codehaus.org/display/SONAR/Web+Service+API
     */
    private org.sonar.wsclient.Sonar sonar;

    private String sonarUrl;

    public Sonar(String url, String login, String password) {
        Preconditions.checkNotNull(url, "sonarUrl is mandatory");
        Preconditions.checkArgument(!url.isEmpty(), "sonarUrl can't be empty");
        this.sonarUrl = url;
        if (isNotBlank(login) && isNotBlank(password)) {
            sonar = org.sonar.wsclient.Sonar.create(url, login, password);
            client = new GenericSoftwareClient(login, password);
        } else {
            sonar = org.sonar.wsclient.Sonar.create(url);
            client = new GenericSoftwareClient();
        }
    }

    public List<Measure> findMeasures(String artifactId, String... measureKeys) throws SonarMeasureNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(artifactId), "artifactId is a mandatory parameter");
        Preconditions.checkNotNull(measureKeys, "measureKeys is a mandatory parameter");
        return findMeasuresFromSonar(artifactId, measureKeys);
    }

    private List<Measure> findMeasuresFromSonar(String artifactId, String... measureKeys) throws SonarMeasureNotFoundException {
        ResourceQuery query = ResourceQuery.createForMetrics(artifactId, measureKeys).setIncludeTrends(true);
        try {
            List<Measure> measures = new ArrayList<Measure>();
            Resource resource = sonar.find(query);
            if (resource == null) {
                throw new SonarMeasureNotFoundException("Metric " + measureKeys + " not found for project "
                        + artifactId + " in Sonar " + sonarUrl);
            }
            for (String measureKey : measureKeys) {
                Measure measure = resource.getMeasure(measureKey);
                measures.add(measure);
            }
            return measures;
        } catch (ConnectionException e) {
            throw new SonarMeasureNotFoundException("Metric " + measureKeys + " not found for project " + artifactId
                    + " in Sonar " + sonarUrl, e);
        }
    }

    public Resource findResource(String resourceId) throws SonarResourceNotFoundException {
        Preconditions.checkNotNull(resourceId, "resourceId is mandatory");
        ResourceQuery query = new ResourceQuery(resourceId);
        try {
            Resource resource = sonar.find(query);
            if (resource == null) {
                throw new SonarResourceNotFoundException("Resource " + resourceId + " not found in Sonar " + sonarUrl);
            }
            return resource;
        } catch (ConnectionException e) {
            throw new SonarResourceNotFoundException("Resource " + resourceId + " not found in Sonar " + sonarUrl, e);

        }
    }

    public Map<String, SonarQualityMetric> findMetrics() throws SonarMetricsNotFoundException {
        try {
            String metricUrl = sonarUrl + "/api/metrics?format=xml";
            SonarMetrics sonarMetrics = client.resource(metricUrl, SonarMetrics.class);
            Map<String, SonarQualityMetric> qualityMetrics = new HashMap<String, SonarQualityMetric>();
            for (SonarQualityMetric metric : sonarMetrics.metric) {
                qualityMetrics.put(metric.getKey(), metric);
            }
            return qualityMetrics;
        } catch (ResourceNotFoundException e) {
            throw new SonarMetricsNotFoundException("Can't find sonar metrics with Sonar: " + sonarUrl, e);
        }
    }

    public Projects findProjects() throws SonarProjectsNotFoundException {
        String projectsUrl = sonarUrl + "/api/projects";
        try {
            return client.resource(projectsUrl, Projects.class, MediaType.APPLICATION_XML_TYPE);
        } catch (ResourceNotFoundException e) {
            throw new SonarProjectsNotFoundException("Can't find Sonar projects", e);
        }
    }

    public Project findProject(String projectKey) throws SonarProjectNotFoundException {
        Preconditions.checkNotNull(projectKey, "projectKey is mandatory");
        try {
            List<Project> projects = findProjects().getProjects();
            for (Project project : projects) {
                if (project.getKey().equals(projectKey)) {
                    return project;
                }
            }
            throw new SonarProjectNotFoundException("Can't find Sonar project with resourceId: '" + projectKey + "'");
        } catch (SonarProjectsNotFoundException e) {
            throw new SonarProjectNotFoundException("Can't find Sonar project with resourceId: '" + projectKey + "'",
                    e);
        }
    }

    public Measure findMeasure(String projectKey, String measureKey) throws SonarMeasureNotFoundException {
        return findMeasures(projectKey, measureKey).get(0);
    }
}
