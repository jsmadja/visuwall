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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private Connections connections = new Connections();

    public void addUrl(Connection connection) {
        this.connections.add(connection);
    }

    public void remove(Connection connection) {
        if (!connections.remove(connection)) {
            LOG.warn(connection + " has not been removed from global configuration");
        }
    }

    public Connections getConnections() {
        return connections;
    }

    public Connection getConnectionByName(String name) {
        return connections.getConnection(name);
    }

    public boolean containsConfiguration(String name) {
        return connections.contains(name);
    }

}
