package com.visuwall.domain;

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
