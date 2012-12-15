package com.visuwall.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Connections implements Iterable<Connection> {

    private List<Connection> connections = new ArrayList<Connection>();

    public void add(Connection connection) {
        this.connections.add(connection);
    }

    @Override
    public Iterator<Connection> iterator() {
        return connections.iterator();
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
