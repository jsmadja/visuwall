package com.visuwall.domain;

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.api.plugin.capability.BasicCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static java.util.concurrent.TimeUnit.MINUTES;

public class Wall implements Runnable {

    private Builds builds = new Builds();

    private Configuration configuration = new Configuration();

    private Logger LOG = LoggerFactory.getLogger(Wall.class);

    private PluginDiscover pluginDiscover = new PluginDiscover();

    public void addConnection(Connection connection) {
        String url = connection.getUrl();
        LOG.info("Trying to identify a compatible plugin for url:" + url);
        VisuwallPlugin plugin = pluginDiscover.findPluginCompatibleWith(connection);
        if(plugin == null) {
            LOG.info("Visuwall cannot find a compatible plugin for "+url);
        } else {
            LOG.info(plugin.getName() + " is compatible with url:" + url);
            addNewValidConnection(connection, plugin);
        }
    }

    private void addNewValidConnection(Connection connection, VisuwallPlugin plugin) {
        PluginConfiguration pluginConfiguration = Connection.createPluginConfigurationFrom(connection);
        URL softwareUrl = connection.asUrl();
        BasicCapability visuwallConnection = plugin.getConnection(softwareUrl, pluginConfiguration);
        connection.setVisuwallConnection(visuwallConnection);
        builds.addConnection(connection);
        configuration.addUrl(connection);
        builds.refresh();
    }

    void start() {
        new Thread(this).start();
    }

    public Builds getBuilds() {
        return builds;
    }

    public Build getBuild(String name) {
        return builds.getBuild(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                LOG.debug("Wall is refreshing ...");
                builds.refresh();
                MINUTES.sleep(1);
            } catch (InterruptedException e) {
                LOG.error("Error in main loop", e);
            }
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void removeConnection(String name) throws ResourceNotFoundException {
        if(!configuration.containsConfiguration(name)) {
            throw new ResourceNotFoundException("Cannot find connection '"+name+"'");
        }
        Connection connection = configuration.getConnectionByName(name);
        removeConnection(connection);
    }

    public void updateConnection(Connection connection) {
        removeConnection(connection);
        addConnection(connection);
    }

    private void removeConnection(Connection connection) {
        configuration.remove(connection);
        builds.removeAllBuildsFrom(connection);
    }

    public Connections getConnections() {
        return configuration.getConnections();
    }
}


