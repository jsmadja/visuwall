/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.visuwall.domain;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.connections.ConnectionConfiguration;
import com.visuwall.domain.connections.Connections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

import static java.util.Collections.unmodifiableSet;

public abstract class AbstractRefreshables<T extends Refreshable> implements Refreshables, Iterable<T> {

    private Connections connections = new Connections();

    private Set<T> refreshables = new TreeSet<T>();

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRefreshables.class);

    public T get(String name) throws RefreshableNotFoundException {
        for (T refreshable : refreshables) {
            if (refreshable.hasName(name)) {
                return refreshable;
            }
        }
        LOG.info("Refreshable '"+name+"' has not been found");
        throw new RefreshableNotFoundException(name);
    }

    private void removeRefreshableIfNecessary(T refreshable) {
        if (refreshable.isRemovable()) {
            refreshables.remove(refreshable);
        }
    }

    public Callable<T> refresh(final T refreshable) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                if (refreshable.isRefreshable()) {
                    LOG.info(refreshable + " is refreshing ...");
                    refreshable.refresh();
                    removeRefreshableIfNecessary(refreshable);
                    LOG.info(refreshable + " is now up-to-date");
                }
                return refreshable;
            }
        };
    }

    private Callable<ConnectionConfiguration> discover(final Connection connection, final SoftwareProjectId projectId) {
        return new Callable<ConnectionConfiguration>() {
            @Override
            public ConnectionConfiguration call() throws Exception {
                ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(projectId, connection);
                if (isAddable(connectionConfiguration)) {
                    addNewRefreshableFrom(connectionConfiguration);
                }
                return connectionConfiguration;
            }
        };
    }

    private boolean isAddable(ConnectionConfiguration connectionConfiguration) {
        SoftwareProjectId softwareProjectId = connectionConfiguration.getProjectId();
        for (T refreshable : refreshables) {
            if (refreshable.is(softwareProjectId)) {
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
        for (Refreshable refreshable : refreshables) {
            if (refreshable.hasName(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeAllFrom(Connection connection) {
        connections.remove(connection);
        String url = connection.getUrl();
        List<T> refreshablesToRemove = new ArrayList<T>();
        for (T refreshable : refreshables) {
            if (refreshable.isLinkedTo(url)) {
                refreshablesToRemove.add(refreshable);
            }
        }
        refreshables.removeAll(refreshablesToRemove);
    }

    protected void add(T refreshable) {
        refreshables.add(refreshable);
    }

    protected abstract void addNewRefreshableFrom(ConnectionConfiguration connectionConfiguration);

    @Override
    public void refresh(ExecutorService pool) {
        for (T refreshable : refreshables) {
            pool.submit(refresh(refreshable));
        }
        discoverNewRefreshables(pool);
    }

    private void discoverNewRefreshables(ExecutorService pool) {
        for (final Connection connection : connections) {
            Collection<SoftwareProjectId> projectIds = connection.listSoftwareProjectIds();
            for (final SoftwareProjectId projectId : projectIds) {
                pool.submit(discover(connection, projectId));
            }
        }
    }

}
