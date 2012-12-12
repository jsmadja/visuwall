package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

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
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<Build>> futures = new ArrayList<Future<Build>>();
        for (Build build : builds) {
            futures.add(pool.submit(createFutureBuild(build)));
        }
        for (Future<Build> future : futures) {
            removeBuildIfNecessary(future);
        }
    }

    private void removeBuildIfNecessary(Future<Build> future) {
        try {
            Build build = future.get();
            if(build.isRemoveable()) {
                builds.remove(build);
            }
        }catch(ExecutionException e) {
            LOG.error("Error when getting future: "+future, e);
        } catch (InterruptedException e) {
            LOG.error("Error when getting future: " + future, e);
        }
    }

    private Callable<Build> createFutureBuild(final Build build) {
        return new Callable<Build>() {
            @Override
            public Build call() throws Exception {
                try {
                    if (build.isRefreshable()) {
                        LOG.info(build + " is refreshing ...");
                        build.refresh();
                    }
                } catch (Throwable t) {
                    build.setRemoveable();
                    LOG.info("Build " + build + " is not available anymore and will be removed from the wall", t);
                }
                return build;
            }
        };
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
            LOG.info("Add a new build " + projectId);
            Build build = new Build(connection, projectId);
            build.refresh();
            builds.add(build);
        } catch(Exception e) {
            LOG.error(projectId+" is unbuildable", e);
        }
    }

    private boolean isAddable(SoftwareProjectId projectId) {
        for (Build build : builds) {
            if(build.is(projectId)) {
                return false;
            }
        }
        return true;
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
            if(build.hasName(name)) {
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
            if(build.hasName(name)) {
                return true;
            }
        }
        return false;
    }
}
