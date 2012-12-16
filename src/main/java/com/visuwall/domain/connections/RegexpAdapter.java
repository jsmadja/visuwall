package com.visuwall.domain.connections;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class RegexpAdapter {
    private String buildFilter;

    public RegexpAdapter(String buildFilter) {
        this.buildFilter = buildFilter;
    }

    public String toString() {
        if (buildFilter.startsWith("regexp:")) {
            return buildFilter.substring("regexp:".length());
        }
        if (buildFilter.contains(",")) {
            List<String> regexps = buildRegexps();
            return Joiner.on('|').join(regexps);
        }
        return buildRegexp(buildFilter);
    }

    private List<String> buildRegexps() {
        List<String> regexps = new ArrayList<String>();
        String[] split = buildFilter.split(",");
        for (String filter : split) {
            String regexp = buildRegexp(filter);
            regexps.add(regexp);
        }
        return regexps;
    }

    private String buildRegexp(String filter) {
        String regexp = "(?i)";
        if (filter.endsWith("*")) {
            String buildNamePrefix = filter.substring(0, filter.length() - 1);
            regexp += buildNamePrefix + ".*";
        } else {
            regexp += filter.substring(0, filter.length());
        }
        return regexp;
    }
}
