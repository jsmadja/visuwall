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

package com.visuwall.domain.builds;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.AbstractRefreshables;
import com.visuwall.domain.connections.ConnectionConfiguration;
import com.visuwall.domain.walls.Wall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;

public class Builds extends AbstractRefreshables<Build> {

    private static final Logger LOG = LoggerFactory.getLogger(Builds.class);

    public Builds(Wall wall) {
        super(wall);
    }

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

    public void sortByStatus() {
        Collections.sort(refreshables, new Comparator<Build>() {
            @Override
            public int compare(Build b1, Build b2) {
                if (b1.getStatus() == b2.getStatus()) {
                    return b2.getNativeLastBuildDate().compareTo(b1.getNativeLastBuildDate());
                }
                return b2.getStatus().compareTo(b1.getStatus());
            }
        });
    }
}
