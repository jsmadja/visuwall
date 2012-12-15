package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class Analyses implements Iterable<Analysis> {

    private Set<Analysis> analyses = new TreeSet<Analysis>();

    private Connections connections = new Connections();

    private static final Logger LOG = LoggerFactory.getLogger(Analyses.class);

    public int count() {
        return analyses.size();
    }

    public Set<Analysis> all() {
        return Collections.unmodifiableSet(analyses);
    }

    public Analysis getAnalysis(String name) {
        for (Analysis analysis : analyses) {
            if (analysis.hasName(name)) {
                return analysis;
            }
        }
        throw new NoSuchElementException(name);
    }

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public void refresh() {
        refreshAnalyses();
        addNewAnalyses();
    }

    private void refreshAnalyses() {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<Analysis>> futures = new ArrayList<Future<Analysis>>();
        for (Analysis analysis : analyses) {
            futures.add(pool.submit(refresh(analysis)));
        }
        for (Future<Analysis> future : futures) {
            removeAnalysisIfNecessary(future);
        }
        pool.shutdown();
    }

    private void removeAnalysisIfNecessary(Future<Analysis> future) {
        try {
            Analysis analysis = future.get();
            if (analysis.isRemoveable()) {
                analyses.remove(analysis);
            }
        } catch (ExecutionException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (InterruptedException e) {
            LOG.error("Error when getting future: " + future, e);
        }
    }

    private Callable<Analysis> refresh(final Analysis analysis) {
        return new Callable<Analysis>() {
            @Override
            public Analysis call() throws Exception {
            if (analysis.isRefreshable()) {
                LOG.info(analysis + " is refreshing ...");
                analysis.refresh();
                LOG.info(analysis + " is now up-to-date");
            }
            return analysis;
            }
        };
    }

    private void addNewAnalyses() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
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
            addAnalysisIfNecessary(future);
        }
        pool.shutdown();
    }

    public void removeAllAnalysesFrom(Connection connection) {
    }

    private void addAnalysisIfNecessary(Future<BuildConfiguration> future) {
        try {
            BuildConfiguration buildConfiguration = future.get();
            if (isAddable(buildConfiguration)) {
                addNewAnalysis(buildConfiguration);
            }
        } catch (ExecutionException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (InterruptedException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (ProjectNotFoundException e) {
            LOG.info(e.getMessage());
        }
    }

    private void addNewAnalysis(BuildConfiguration buildConfiguration) {
        BasicCapability connection = buildConfiguration.getVisuwallConnection();
        SoftwareProjectId projectId = buildConfiguration.getProjectId();
        try {
            LOG.info("Add a new analysis " + projectId);
            List<String> selectedMetrics = buildConfiguration.getConnection().getIncludeMetricNames();
            Analysis analysis = new Analysis(connection, projectId, selectedMetrics);
            analysis.refresh();
            analyses.add(analysis);
        } catch(Exception e) {
            LOG.error(projectId+" is unanalysable", e);
        }
    }

    private boolean isAddable(BuildConfiguration buildConfiguration) throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = buildConfiguration.getProjectId();
        for (Analysis analysis : analyses) {
            if(analysis.is(softwareProjectId)) {
                return false;
            }
        }
        return buildConfiguration.accept(softwareProjectId);
    }

    @Override
    public Iterator<Analysis> iterator() {
        return analyses.iterator();
    }
}
