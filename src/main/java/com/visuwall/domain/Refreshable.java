package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import org.codehaus.jackson.annotate.JsonIgnore;

public interface Refreshable {

    String getName();

    void refresh();

    @JsonIgnore
    boolean isRefreshable();

    boolean isLinkedTo(String url);

    boolean hasName(String name);

    boolean isRemovable();

    boolean is(SoftwareProjectId softwareProjectId);
}
