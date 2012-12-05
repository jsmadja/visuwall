package com.visuwall.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginConfiguration {
    public static PluginConfiguration noConfiguration = new PluginConfiguration();

    private Map<String, String> values = new HashMap<String, String>();

    public Map<String,String> getValues() {
        return Collections.unmodifiableMap(values);
    }

    public String get(String key) {
        return values.get(key);
    }
}
