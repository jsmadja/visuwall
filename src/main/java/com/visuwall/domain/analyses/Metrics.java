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

import com.visuwall.api.domain.quality.QualityMeasure;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class Metrics {

    private Set<Metric> metrics = new TreeSet<Metric>();

    public void add(QualityMeasure qualityMeasure, boolean qualitative) {
        String name = qualityMeasure.getName();
        String value = qualityMeasure.getFormattedValue();
        String key = qualityMeasure.getKey();
        int tendency = qualityMeasure.getTendency();
        Metric metric = new Metric(name, value, key, tendency, qualitative);
        metrics.remove(metric);
        metrics.add(metric);
    }

    public Set<Metric> all() {
        return Collections.unmodifiableSet(metrics);
    }
}
