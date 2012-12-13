package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;

public class BuildConfiguration {

    private SoftwareProjectId projectId;
    private Connection connection;

    public BuildConfiguration(SoftwareProjectId projectId, Connection connection) {
        this.projectId = projectId;
        this.connection = connection;
    }

    public SoftwareProjectId getProjectId() {
        return projectId;
    }

    public Connection getConnection() {
        return connection;
    }

    public BasicCapability getVisuwallConnection() {
        return connection.getVisuwallConnection();
    }

    public boolean accept(SoftwareProjectId softwareProjectId) {
        return connection.accept(softwareProjectId.getProjectId());
    }
}

