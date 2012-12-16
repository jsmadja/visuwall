package com.visuwall.domain.builds;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.AbstractRefreshables;
import com.visuwall.domain.connections.ConnectionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Builds extends AbstractRefreshables<Build> {

    private static final Logger LOG = LoggerFactory.getLogger(Builds.class);

    @Override
    protected void addNewRefreshableFrom(ConnectionConfiguration connectionConfiguration) {
        BasicCapability connection = connectionConfiguration.getVisuwallConnection();
        SoftwareProjectId projectId = connectionConfiguration.getProjectId();
        try {
            LOG.info("Add a new build " + projectId);
            Build build = new Build(connection, projectId);
            build.refresh();
            add(build);
        } catch (Exception e) {
            LOG.error(projectId + " is unbuildable", e);
        }
    }

}
