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

import org.codehaus.jackson.annotate.JsonIgnore;

public class Metric implements Comparable<Metric> {

    private String key;
    private String name;
    private String value;
    private int tendency;
    private boolean qualitative;
    private boolean selected;

    Metric() {}

    public Metric(String name, String value, String key, int tendency, boolean qualitative) {
        this.name = name;
        this.value = value;
        this.tendency = tendency;
        this.key = key;
        this.qualitative = qualitative;
    }

    public Metric(String key, String name) {
        this.key = key;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }

    @Override
    public int compareTo(Metric metric) {
        int comparison = new Integer(tendency).compareTo(metric.tendency);
        if (comparison != 0) {
            return comparison;
        }
        return name.compareToIgnoreCase(metric.name);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getTendency() {
        return tendency;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTendency(int tendency) {
        this.tendency = tendency;
    }

    public boolean isQualitative() {
        return qualitative;
    }

    public void setQualitative(boolean qualitative) {
        this.qualitative = qualitative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric metric = (Metric) o;

        if (!key.equals(metric.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @JsonIgnore
    public boolean isEqualToZero() {
        return value.equals("0") || value.equals("0.0") || value.equals("0.0%");
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean tendencyIsNotEqualTo(int tendency) {
        return this.tendency != tendency;
    }
}
