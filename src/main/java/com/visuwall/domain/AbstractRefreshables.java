package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.connections.ConnectionConfiguration;
import com.visuwall.domain.connections.Connections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

import static java.util.Collections.unmodifiableSet;

public abstract class AbstractRefreshables<T extends Refreshable> implements Refreshables, Iterable<T> {

    protected Connections connections = new Connections();

    private Set<T> refreshables = new TreeSet<T>();

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRefreshables.class);

    public T get(String name) {
        for (T refreshable : refreshables) {
            if (refreshable.hasName(name)) {
                return refreshable;
            }
        }
        throw new NoSuchElementException(name);
    }

    @Override
    public void refreshAll() {
        refresh();
        discoverNewRefreshables();
    }

    private void refresh() {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<T>> futures = new ArrayList<Future<T>>();
        for (T refreshable : refreshables) {
            futures.add(pool.submit(refresh(refreshable)));
        }
        for (Future<T> future : futures) {
            removeRefreshableIfNecessary(future);
        }
        pool.shutdown();
    }

    private void removeRefreshableIfNecessary(Future<T> future) {
        try {
            T refreshable = future.get();
            if (refreshable.isRemoveable()) {
                refreshables.remove(refreshable);
            }
        } catch (ExecutionException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (InterruptedException e) {
            LOG.error("Error when getting future: " + future, e);
        }
    }

    private Callable<T> refresh(final T refreshable) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                if (refreshable.isRefreshable()) {
                    LOG.info(refreshable + " is refreshing ...");
                    refreshable.refresh();
                    LOG.info(refreshable + " is now up-to-date");
                }
                return refreshable;
            }
        };
    }

    private void discoverNewRefreshables() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<Future<ConnectionConfiguration>> futures = new ArrayList<Future<ConnectionConfiguration>>();
        for (final Connection connection : connections) {
            Collection<SoftwareProjectId> projectIds = connection.listSoftwareProjectIds();
            for (final SoftwareProjectId projectId : projectIds) {
                futures.add(pool.submit(new Callable<ConnectionConfiguration>() {
                    @Override
                    public ConnectionConfiguration call() throws Exception {
                        return new ConnectionConfiguration(projectId, connection);
                    }
                }));
            }
        }
        for (Future<ConnectionConfiguration> future : futures) {
            addIfNecessary(future);
        }
        pool.shutdown();
    }

    private void addIfNecessary(Future<ConnectionConfiguration> future) {
        try {
            ConnectionConfiguration connectionConfiguration = future.get();
            if (isAddable(connectionConfiguration)) {
                addNewRefreshableFrom(connectionConfiguration);
            }
        } catch (ExecutionException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (InterruptedException e) {
            LOG.error("Error when getting future: " + future, e);
        } catch (ProjectNotFoundException e) {
            LOG.info(e.getMessage());
        }
    }

    private boolean isAddable(ConnectionConfiguration connectionConfiguration) throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = connectionConfiguration.getProjectId();
        for (T refreshable : refreshables) {
            if(refreshable.is(softwareProjectId)) {
                return false;
            }
        }
        return connectionConfiguration.accept(softwareProjectId);
    }

    @Override
    public Iterator<T> iterator() {
        return refreshables.iterator();
    }

    @Override
    public int count() {
        return refreshables.size();
    }

    @Override
    public Set<T> all() {
        return unmodifiableSet(refreshables);
    }

    @Override
    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    @Override
    public boolean contains(String name) {
        for (Refreshable refreshable : getRefreshables()) {
            if (refreshable.hasName(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeAllFrom(Connection connection) {
        String url = connection.getUrl();
        List<T> refreshablesToRemove = new ArrayList<T>();
        for (T refreshable : getRefreshables()) {
            if (refreshable.isLinkedTo(url)) {
                refreshablesToRemove.add(refreshable);
            }
        }
        refreshables.removeAll(refreshablesToRemove);
    }

    public Set<T> getRefreshables() {
        return refreshables;
    }

    protected void add(T refreshable) {
        refreshables.add(refreshable);
    }

    protected abstract void addNewRefreshableFrom(ConnectionConfiguration connectionConfiguration);
}
