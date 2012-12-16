package com.visuwall.domain.connections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
