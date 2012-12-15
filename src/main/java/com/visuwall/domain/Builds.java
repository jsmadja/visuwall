package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class Builds implements Iterable<Build> {

    private Set<Build> builds = new TreeSet<Build>();

    private Connections connections = new Connections();

    private static final Logger LOG = LoggerFactory.getLogger(Builds.class);

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public void refresh() {
        refreshBuilds();
        addNewBuilds();
    }

    private void refreshBuilds() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<Future<Build>> futures = new ArrayList<Future<Build>>();
        for (Build build : builds) {
            futures.add(pool.submit(refresh(build)));
        }
        for (Future<Build> future : futures) {
            removeBuildIfNecessary(future);
        }
        pool.shutdown();
    }

    private void removeBuildIfNecessary(Future<Build> future) {
        try {
            Build build = future.get();
            if (build.isRemoveable()) {
                builds.remove(build);
            }
        } catch (ExecutionException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (InterruptedException e) {
            LOG.error("Error when getting future: " + future, e);
        }
    }

    private Callable<Build> refresh(final Build build) {
        return new Callable<Build>() {
            @Override
            public Build call() throws Exception {
            if (build.isRefreshable()) {
                LOG.info(build + " is refreshing ...");
                build.refresh();
                LOG.info(build + " is now up-to-date");
            }
            return build;
            }
        };
    }

    private void addNewBuilds() {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<BuildConfiguration>> futures = new ArrayList<Future<BuildConfiguration>>();
        for (final Connection connection : connections) {
            Collection<SoftwareProjectId> projectIds = connection.listSoftwareProjectIds();
            for (final SoftwareProjectId projectId : projectIds) {
                futures.add(pool.submit(new Callable<BuildConfiguration>() {
                    @Override
                    public BuildConfiguration call() throws Exception {
                        return new BuildConfiguration(projectId, connection);
                    }
                }));
            }
        }
        for (Future<BuildConfiguration> future : futures) {
            addBuildIfNecessary(future);
        }
        pool.shutdown();
    }

    public void removeAllBuildsFrom(Connection connection) {
        List<Build> buildsToRemove = new ArrayList<Build>();
        for (Build build : builds) {
            if (build.isLinkedTo(connection.getUrl())) {
                buildsToRemove.add(build);
            }
        }
        builds.removeAll(buildsToRemove);
    }

    private void addBuildIfNecessary(Future<BuildConfiguration> future) {
        try {
            BuildConfiguration buildConfiguration = future.get();
            if (isAddable(buildConfiguration)) {
                addNewBuild(buildConfiguration);
            }
        } catch (ExecutionException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (InterruptedException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (ProjectNotFoundException e) {
            LOG.info(e.getMessage());
        }
    }

    private void addNewBuild(BuildConfiguration buildConfiguration) {
        BasicCapability connection = buildConfiguration.getVisuwallConnection();
        SoftwareProjectId projectId = buildConfiguration.getProjectId();
        try {
            LOG.info("Add a new build " + projectId);
            Build build = new Build(connection, projectId);
            build.refresh();
            builds.add(build);
        } catch (Exception e) {
            LOG.error(projectId + " is unbuildable", e);
        }
    }

    private boolean isAddable(BuildConfiguration buildConfiguration) throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = buildConfiguration.getProjectId();
        for (Build build : builds) {
            if (build.is(softwareProjectId) || build.isDisabled()) {
                return false;
            }
        }
        return buildConfiguration.accept(softwareProjectId);
    }

    @Override
    public Iterator<Build> iterator() {
        return builds.iterator();
    }

    public Set<Build> all() {
        return Collections.unmodifiableSet(builds);
    }

    public Build getBuild(String name) {
        for (Build build : builds) {
            if (build.hasName(name)) {
                return build;
            }
        }
        throw new NoSuchElementException(name);
    }

    public int count() {
        return builds.size();
    }

    public boolean contains(String name) {
        for (Build build : builds) {
            if (build.hasName(name)) {
                return true;
            }
        }
        return false;
    }

}
