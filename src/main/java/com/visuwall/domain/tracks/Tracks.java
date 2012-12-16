package com.visuwall.domain.tracks;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.AbstractRefreshables;
import com.visuwall.domain.connections.ConnectionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tracks extends AbstractRefreshables<Track> {

    private static final Logger LOG = LoggerFactory.getLogger(Tracks.class);

    @Override
    protected void addNewRefreshableFrom(ConnectionConfiguration connectionConfiguration) {
        BasicCapability connection = connectionConfiguration.getVisuwallConnection();
        SoftwareProjectId projectId = connectionConfiguration.getProjectId();
        try {
            LOG.info("Add a new track " + projectId);
            Track track = new Track(connection, projectId);
            track.refresh();
            add(track);
        } catch (Exception e) {
            LOG.error(projectId + " is untracked", e);
        }
    }
}
