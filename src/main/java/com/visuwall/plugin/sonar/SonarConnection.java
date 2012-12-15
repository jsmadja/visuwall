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

package com.visuwall.plugin.sonar;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.quality.QualityMeasure;
import com.visuwall.api.domain.quality.QualityMetric;
import com.visuwall.api.domain.quality.QualityResult;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.MetricCapability;
import com.visuwall.client.sonar.Sonar;
import com.visuwall.client.sonar.domain.SonarQualityMetric;
import com.visuwall.client.sonar.exception.*;
import com.visuwall.client.sonar.resource.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;

import java.util.*;

public class SonarConnection implements MetricCapability {

    private static final Logger LOG = LoggerFactory.getLogger(SonarConnection.class);

    private final UUID id = UUID.randomUUID();

    @VisibleForTesting
    Sonar sonarClient;

    private Map<String, QualityMetric> metricsMap;
    private String[] metricKeys = new String[] {};

    private boolean connected;

    private String url;

    public SonarConnection() {
    }

    @Override
    public void connect(String url, String login, String password) {
        this.url = url;
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize sonar with url " + url);
        }
        if (sonarClient == null) {
            sonarClient = new Sonar(url, login, password);
        }
        connected = true;
    }

    @Override
    public Map<String, List<QualityMetric>> getMetricsByCategory() {
        checkConnected();
        if (metricsMap == null) {
            initializeMetrics();
        }
        Map<String, List<QualityMetric>> metricsByDomain = new HashMap<String, List<QualityMetric>>();
        for (QualityMetric metricValue : metricsMap.values()) {
            String domain = metricValue.getDomain();
            List<QualityMetric> domainMetrics = metricsByDomain.get(domain);
            if (domainMetrics == null) {
                domainMetrics = new ArrayList<QualityMetric>();
                metricsByDomain.put(domain, domainMetrics);
            }
            domainMetrics.add(metricValue);
        }
        return metricsByDomain;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        checkConnected();
        Map<SoftwareProjectId, String> projects = new HashMap<SoftwareProjectId, String>();
        try {
            List<Project> names = sonarClient.findProjects().getProjects();
            for (Project project : names) {
                String key = project.getKey();
                projects.put(new SoftwareProjectId(key), project.getName());
            }
        } catch (SonarProjectsNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
        return projects;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SonarConnection) {
            SonarConnection s = (SonarConnection) o;
            return id.equals(s.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String artifactId = softwareProjectId.getProjectId();
            Resource resource = sonarClient.findResource(artifactId);
            return resource.getName(true);
        } catch (SonarResourceNotFoundException e) {
            throw new ProjectNotFoundException("Can't get description of software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public QualityResult analyzeQuality(SoftwareProjectId projectId, String... metrics) {
        checkConnected();
        checkSoftwareProjectId(projectId);
        if (metricsMap == null) {
            initializeMetrics();
        }
        if (metrics.length == 0) {
            metrics = metricKeys;
        }
        QualityResult qualityResult = new QualityResult();
        String artifactId = projectId.getProjectId();
        if (!Strings.isNullOrEmpty(artifactId)) {
            for (String key : metrics) {
                addQualityMeasure(qualityResult, artifactId, key);
            }
        }
        return qualityResult;
    }

    private void addQualityMeasure(QualityResult qualityResult, String artifactId, String key) {
        try {
            Measure measure = sonarClient.findMeasure(artifactId, key);
            if (measure != null && measure.getValue() != null) {
                SonarQualityMeasure qualityMeasure = QualityMeasures.asQualityMeasure(measure, key);
                qualityMeasure.setName(metricsMap.get(key).getName());
                qualityResult.add(key, asQualityMeasure(qualityMeasure));
            }
        } catch (SonarMeasureNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.trace(e.getMessage());
            }
        }
    }

    private QualityMeasure asQualityMeasure(SonarQualityMeasure sonarQualityMeasure) {
        QualityMeasure qualityMeasure = new QualityMeasure();
        qualityMeasure.setFormattedValue(sonarQualityMeasure.getFormattedValue());
        qualityMeasure.setKey(sonarQualityMeasure.getKey());
        qualityMeasure.setName(sonarQualityMeasure.getName());
        qualityMeasure.setValue(sonarQualityMeasure.getValue());
        qualityMeasure.setTendency(sonarQualityMeasure.getTendency());
        return qualityMeasure;
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String artifactId = softwareProjectId.getProjectId();
            Resource resource = sonarClient.findResource(artifactId);
            return resource.getName();
        } catch (SonarResourceNotFoundException e) {
            throw new ProjectNotFoundException("Can't get name of software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String artifactId = softwareProjectId.getProjectId();
            sonarClient.findProject(artifactId);
        } catch (SonarProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find if project is disabled, softwareProjectId:"
                    + softwareProjectId + ", url: " + url);
        }
        return false;
    }

    @Override
    public String getUrl() {
        return url;
    }

    private void initializeMetrics() {
        try {
            metricsMap = asMetrics(sonarClient.findMetrics());
            Set<String> metricKeysSet = metricsMap.keySet();
            int countMetricKeys = metricKeysSet.size();
            metricKeys = metricKeysSet.toArray(new String[countMetricKeys]);
        } catch (SonarMetricsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't initialize metrics", e);
            }
        }
    }

    private Map<String, QualityMetric> asMetrics(Map<String, SonarQualityMetric> sonarMetrics) {
        Map<String, QualityMetric> metrics = new HashMap<String, QualityMetric>();
        for (Map.Entry<String, SonarQualityMetric> metric : sonarMetrics.entrySet()) {
            String key = metric.getKey();
            SonarQualityMetric value = metric.getValue();
            QualityMetric qualityMetric = asQualityMetric(value);
            metrics.put(key, qualityMetric);
        }
        return metrics;
    }

    private QualityMetric asQualityMetric(SonarQualityMetric sonarQualityMetric) {
        QualityMetric qualityMetric = new QualityMetric();
        qualityMetric.setDescription(sonarQualityMetric.getDescription());
        qualityMetric.setDirection(sonarQualityMetric.getDirection());
        qualityMetric.setDomain(sonarQualityMetric.getDomain());
        qualityMetric.setHidden(sonarQualityMetric.getHidden());
        qualityMetric.setKey(sonarQualityMetric.getKey());
        qualityMetric.setName(sonarQualityMetric.getName());
        qualityMetric.setQualitative(sonarQualityMetric.getQualitative());
        qualityMetric.setUserManaged(sonarQualityMetric.getUserManaged());
        qualityMetric.setValTyp(sonarQualityMetric.getValTyp());
        return qualityMetric;
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

}
