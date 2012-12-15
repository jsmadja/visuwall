package com.visuwall.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

public interface Refreshable {
    String getName();

    void refresh();

    @JsonIgnore
    boolean isRefreshable();
}
