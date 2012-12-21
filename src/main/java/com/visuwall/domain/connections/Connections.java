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

import javax.xml.bind.annotation.*;
import java.util.*;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

@XmlRootElement(name = "connections")
@XmlAccessorType(FIELD)
public class Connections implements Iterable<Connection> {

    @XmlElements({@XmlElement(name = "connection")})
    private List<Connection> connections = new ArrayList<Connection>();

    public void add(Connection connection) {
        this.connections.add(connection);
    }

    @Override
    public Iterator<Connection> iterator() {
        return connections .iterator();
    }

    public boolean remove(Connection connection) {
        return connections.remove(connection);
    }

    public boolean contains(String name) {
        for (Connection connection : connections) {
            if (connection.hasName(name)) {
                return true;
            }
        }
        return false;
    }

    public Connection getConnection(String name) {
        for (Connection connection : connections) {
            if (connection.hasName(name)) {
                return connection;
            }
        }
        throw new IllegalStateException("Cannot find connection '" + name + "'");
    }

}
