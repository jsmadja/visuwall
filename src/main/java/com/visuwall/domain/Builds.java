package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Builds implements Iterable<Build>{

    private Set<Build> builds = new TreeSet<Build>();

    private Set<BasicCapability> connections = new HashSet<BasicCapability>();

    private static final Logger LOG = LoggerFactory.getLogger(Builds.class);

    public void addConnection(BasicCapability connection) {
        this.connections.add(connection);
    }

    public void refresh() {
        refreshBuilds();
        addNewBuilds();
    }

    private void refreshBuilds() {
        List<Build> buildsToRemove = new ArrayList<Build>();
        for (Build build : builds) {
            try {
                if(build.isRefreshable()) {
                    build.refresh();
                }
            } catch(Throwable t) {
                LOG.info("Build "+build+" is not available anymore and will be removed from the wall, cause: "+t.getMessage());
                buildsToRemove.add(build);
            }
        }
        builds.removeAll(buildsToRemove);
    }

    private void addNewBuilds() {
        for (BasicCapability connection : connections) {
            Collection<SoftwareProjectId> projectIds = connection.listSoftwareProjectIds().keySet();
            for (SoftwareProjectId projectId : projectIds) {
                if(isAddable(projectId)) {
                    addNewBuild(connection, projectId);
                }
            }
        }
    }

    private void addNewBuild(BasicCapability connection, SoftwareProjectId projectId) {
        try {
            Build build = new Build(connection, projectId);
            build.refresh();
            builds.add(build);
        } catch(Exception e) {
            LOG.warn(projectId+" is unbuildable, cause: "+e.getMessage());
        }
    }

    private boolean isAddable(SoftwareProjectId projectId) {
        for (Build build : builds) {
            if(build.is(projectId) || isNotValidName(projectId)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNotValidName(SoftwareProjectId projectId) {
        String name = projectId.getProjectId();
        if(name.startsWith("canvas")) {
            return true;
        }
        return !(name.startsWith("Fx")||name.startsWith("fx")||name.startsWith("caution")||name.startsWith("hermes")||name.startsWith("Hermes"));
    }

    @Override
    public Iterator<Build> iterator() {
        return builds.iterator();
    }

    public Set<Build> all() {
        return Collections.unmodifiableSet(builds);
    }
}
