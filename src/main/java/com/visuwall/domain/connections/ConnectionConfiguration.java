package com.visuwall.domain.connections;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;

public class ConnectionConfiguration {

    private SoftwareProjectId projectId;
    private Connection connection;

    public ConnectionConfiguration(SoftwareProjectId projectId, Connection connection) {
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

