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

package com.visuwall.domain.connections;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;

public class ConnectionConfiguration {

    private SoftwareProjectId projectId;
    private Connection connection;

    public ConnectionConfiguration(SoftwareProjectId projectId, Connection connection) {
        this.projectId = projectId;
        this.connection = connection;
    }

    public SoftwareProjectId getProjectId() {
        return projectId;
    }

    public Connection getConnection() {
        return connection;
    }

    public BasicCapability getVisuwallConnection() {
        return connection.getVisuwallConnection();
    }

    public boolean accept(SoftwareProjectId softwareProjectId) {
        return connection.accept(softwareProjectId.getProjectId());
    }
}

