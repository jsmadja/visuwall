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
