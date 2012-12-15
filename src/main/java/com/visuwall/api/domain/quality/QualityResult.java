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

package com.visuwall.api.domain.quality;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.google.common.base.Objects;

public final class QualityResult implements Iterable<QualityMeasure> {

    private Map<String, QualityMeasure> measures = new HashMap<String, QualityMeasure>();

    public QualityMeasure getMeasure(String key) {
        return measures.get(key);
    }

    public void add(String key, QualityMeasure measure) {
        measures.put(key, measure);
    }

    public Set<Entry<String, QualityMeasure>> getMeasures() {
        return measures.entrySet();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("measures", measures.values()) //
                .toString();
    }

    @Override
    public Iterator<QualityMeasure> iterator() {
        return measures.values().iterator();
    }
}
