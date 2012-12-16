package com.visuwall.domain.analyses;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.AbstractRefreshables;
import com.visuwall.domain.connections.ConnectionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Analyses extends AbstractRefreshables<Analysis> {

    private static final Logger LOG = LoggerFactory.getLogger(Analyses.class);

    @Override
    protected void addNewRefreshableFrom(ConnectionConfiguration connectionConfiguration) {
        BasicCapability connection = connectionConfiguration.getVisuwallConnection();
        SoftwareProjectId projectId = connectionConfiguration.getProjectId();
        try {
            LOG.info("Add a new analysis " + projectId);
            List<String> selectedMetrics = connectionConfiguration.getConnection().getIncludeMetricNames();
            Analysis analysis = new Analysis(connection, projectId, selectedMetrics);
            analysis.refresh();
            add(analysis);
        } catch (Exception e) {
            LOG.error(projectId + " is unanalysable", e);
        }
    }

}
