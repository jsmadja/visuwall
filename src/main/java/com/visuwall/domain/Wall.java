package com.visuwall.domain;

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.MetricCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Wall implements Runnable {

    private Builds builds = new Builds();
    private Analyses analyses = new Analyses();

    private Configuration configuration = new Configuration();

    private Logger LOG = LoggerFactory.getLogger(Wall.class);

    private PluginDiscover pluginDiscover = new PluginDiscover();

    public void addConnection(Connection connection) {
        String url = connection.getUrl();
        LOG.info("Trying to identify a compatible plugin for url:" + url);
        VisuwallPlugin plugin = pluginDiscover.findPluginCompatibleWith(connection);
        if (plugin == null) {
            LOG.info("Visuwall cannot find a compatible plugin for " + url);
        } else {
            LOG.info(plugin.getName() + " is compatible with url:" + url);
            addNewValidConnection(connection, plugin);
            LOG.info("New connection established for "+url);
        }
    }

    private void addNewValidConnection(Connection connection, VisuwallPlugin plugin) {
        PluginConfiguration pluginConfiguration = Connection.createPluginConfigurationFrom(connection);
        URL softwareUrl = connection.asUrl();
        BasicCapability visuwallConnection = plugin.getConnection(softwareUrl, pluginConfiguration);
        connection.setVisuwallConnection(visuwallConnection);
        if (visuwallConnection instanceof MetricCapability) {
            analyses.addConnection(connection);
        }
        if (visuwallConnection instanceof BuildCapability) {
            builds.addConnection(connection);
        }
        configuration.addUrl(connection);
    }

    void start() {
        LOG.info("Starting thread of wall");
        new Thread(this).start();
    }

    public Builds getBuilds() {
        return builds;
    }

    public Analyses getAnalyses() {
        return analyses;
    }

    public Build getBuild(String name) {
        return builds.getBuild(name);
    }

    public Analysis getAnalysis(String name) {
        return analyses.getAnalysis(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                LOG.debug("Wall is full refreshing ...");
                builds.refresh();
                analyses.refresh();
                LOG.debug("Wall has been fully refresh");
                waitForNextIteration();
            } catch (InterruptedException e) {
                LOG.error("Error in main loop", e);
            }
        }
    }

    private void waitForNextIteration() throws InterruptedException {
        if(builds.count() == 0 && analyses.count() == 0) {
            TimeUnit.SECONDS.sleep(1);
        } else {
            TimeUnit.MINUTES.sleep(1);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void removeConnection(String name) throws ResourceNotFoundException {
        if (!configuration.containsConfiguration(name)) {
            throw new ResourceNotFoundException("Cannot find connection '" + name + "'");
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
        analyses.removeAllAnalysesFrom(connection);
    }

    public Connections getConnections() {
        return configuration.getConnections();
    }

}


