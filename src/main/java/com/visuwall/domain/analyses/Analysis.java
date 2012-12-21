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

package com.visuwall.domain.analyses;

import com.google.common.base.Predicate;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.quality.QualityMeasure;
import com.visuwall.api.domain.quality.QualityMetric;
import com.visuwall.api.domain.quality.QualityResult;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.MetricCapability;
import com.visuwall.domain.Refreshable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.collect.Collections2.filter;

public class Analysis implements Comparable<Analysis>, Refreshable {

    private String name;

    @JsonIgnore
    private BasicCapability connection;

    @JsonIgnore
    private SoftwareProjectId projectId;

    @JsonIgnore
    private boolean removable;

    @JsonIgnore
    private boolean refreshing;

    private Metrics metrics = new Metrics();

    @JsonIgnore
    private List<String> selectedMetrics;

    private static final Logger LOG = LoggerFactory.getLogger(Analysis.class);

    private Map<String, List<QualityMetric>> metricsByCategory;

    @JsonIgnore
    private MetricCapability metricCapability;

    @JsonIgnore
    private DateTime lastAnalysis = DateTime.now();

    Analysis() {}

    public Analysis(BasicCapability connection, SoftwareProjectId projectId, List<String> selectedMetrics) {
        this.connection = connection;
        this.projectId = projectId;
        this.name = projectId.getProjectId();
        this.selectedMetrics = selectedMetrics;
        this.removable = false;
        this.metricCapability = (MetricCapability) connection;
        this.metricsByCategory = metricCapability.getMetricsByCategory();
    }

    @Override
    public void refresh() {
        try {
            refreshing = true;
            LOG.debug("Starting refresh of " + name);
            name = connection.getName(projectId);
            refreshMetrics();
            refreshing = false;
            LOG.debug("Ending refresh of " + name);
            lastAnalysis = DateTime.now();
        } catch (ProjectNotFoundException e) {
            setRemovable();
        }
    }

    public Set<Metric> getMetrics() {
        Set<Metric> all = metrics.all();
        return filterSet(all, new Predicate<Metric>() {
            @Override
            public boolean apply(Metric metric) {
                return metric.isQualitative() && !metric.isEqualToZero() && metric.tendencyIsNotEqualTo(0);
            }
        });
    }

    private TreeSet<Metric> filterSet(Set<Metric> all, Predicate<Metric> predicate) {
        return new TreeSet<Metric>(filter(all, predicate));
    }

    private void refreshMetrics() {
        String[] metrics = selectedMetrics.toArray(new String[selectedMetrics.size()]);
        QualityResult qualityResult = metricCapability.analyzeQuality(projectId, metrics);
        if (qualityResult != null) {
            for (QualityMeasure qualityMeasure : qualityResult) {
                if (accept(qualityMeasure)) {
                    insertMetric(qualityMeasure);
                }
            }
        }
    }

    private void insertMetric(QualityMeasure qualityMeasure) {
        boolean qualitative = getQualitativeOf(qualityMeasure);
        metrics.add(qualityMeasure, qualitative);
    }

    private boolean getQualitativeOf(QualityMeasure qualityMeasure) {
        for (List<QualityMetric> metrics : metricsByCategory.values()) {
            for (QualityMetric metric : metrics) {
                if (metric != null && metric.getName().equals(qualityMeasure.getName())) {
                    return metric.getQualitative();
                }
            }
        }
        return false;
    }

    private boolean accept(QualityMeasure qualityMeasure) {
        if (selectedMetrics.isEmpty()) {
            return true;
        }
        String metricKey = qualityMeasure.getKey();
        return selectedMetrics.contains(metricKey);
    }

    public boolean is(SoftwareProjectId projectId) {
        return this.projectId.equals(projectId);
    }

    @Override
    public boolean isRefreshable() {
        return !refreshing && lastAnalysis.plusMinutes(10).isBeforeNow();
    }

    public void setRemovable() {
        this.removable = true;
    }

    public boolean isRemovable() {
        return removable;
    }

    @Override
    public boolean hasName(String name) {
        return this.name != null && this.name.equalsIgnoreCase(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Analysis analysis) {
        return this.name.compareToIgnoreCase(analysis.name);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Analysis) {
            Analysis a = (Analysis) o;
            return hasName(a.name);
        }
        return false;
    }

    @Override
    public boolean isLinkedTo(String url) {
        return this.connection.getUrl().equalsIgnoreCase(url);
    }
}
