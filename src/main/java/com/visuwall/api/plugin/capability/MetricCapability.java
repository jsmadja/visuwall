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

package com.visuwall.api.plugin.capability;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.quality.QualityMetric;
import com.visuwall.api.domain.quality.QualityResult;

public interface MetricCapability extends BasicCapability {

    /**
     * @return Quality Metrics sorted by category
     */
    Map<String, List<QualityMetric>> getMetricsByCategory();

    /**
     * Generate a complete quality reporting for a project defined by <code>projectId</code>
     * 
     * @param projectId
     * @param metrics
     *            You can specify the metrics you only want to analyze.
     * @return
     */
    QualityResult analyzeQuality(SoftwareProjectId projectId, String... metrics);

}
