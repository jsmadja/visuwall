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

package com.visuwall.plugin.demo.analysis;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.quality.QualityMeasure;
import com.visuwall.api.domain.quality.QualityMetric;
import com.visuwall.api.domain.quality.QualityAnalysis;
import com.visuwall.api.plugin.capability.MetricCapability;
import com.visuwall.plugin.demo.DemoConnection;
import com.visuwall.plugin.demo.SoftwareProjectIds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.visuwall.plugin.demo.SoftwareProjectIds.earth;

public class DemoAnalysisConnection extends DemoConnection implements MetricCapability {

    private Map<String, List<QualityMetric>> metricsByCategory = new HashMap<String, List<QualityMetric>>();
    private Map<SoftwareProjectId, QualityAnalysis> analyses = new HashMap<SoftwareProjectId, QualityAnalysis>();

    public DemoAnalysisConnection() {
        metricsByCategory.put("Category A", new ArrayList<QualityMetric>() {{
            add(new QualityMetric.Builder().
                    withKey("loc").
                    withQualitative(true).
                    withName("Lines of code").
                    build());
            add(new QualityMetric.Builder().
                    withKey("complexity").
                    withQualitative(true).
                    withName("Complexity").
                    build());
        }});

        QualityAnalysis earthAnalysis = new QualityAnalysis.Builder().
                add("loc", new QualityMeasure.Builder().
                        withFormattedValue("1.000").
                        withKey("loc").
                        withName("Lines of code").
                        withTendency(1).
                        withValue(1000D).
                        build()).
                add("complexity", new QualityMeasure.Builder().
                        withFormattedValue("1.6").
                        withKey("complexity").
                        withName("Complexity").
                        withTendency(1).
                        withValue(1.6).
                        build()).
                build();

        analyses.put(earth, earthAnalysis);
    }

    @Override
    public Map<String, List<QualityMetric>> getMetricsByCategory() {
        return metricsByCategory;
    }

    @Override
    public QualityAnalysis analyzeQuality(SoftwareProjectId projectId, String... metrics) {
        return analyses.get(projectId);
    }

}
