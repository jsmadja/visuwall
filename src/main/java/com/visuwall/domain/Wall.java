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

    private Plugins plugins = new Plugins();

    private Logger LOG = LoggerFactory.getLogger(Wall.class);

    public void addConnection(ConnectionConfiguration connectionConfiguration) {
        String url = connectionConfiguration.getUrl();
        LOG.info("Trying to identify a compatible plugin for url:" + url);
        VisuwallPlugin plugin = findPluginCompatibleWith(connectionConfiguration);
        if(plugin == null) {
            LOG.info("Visuwall cannot find a compatible plugin for "+url);
        } else {
            LOG.info(plugin.getName() + " is compatible with url:" + url);
            addNewValidConnection(connectionConfiguration, plugin);
        }
    }

    private void addNewValidConnection(ConnectionConfiguration connectionConfiguration, VisuwallPlugin plugin) {
        PluginConfiguration pluginConfiguration = createPluginConfigurationFrom(connectionConfiguration);
        URL softwareUrl = connectionConfiguration.asUrl();
        BasicCapability connection = plugin.getConnection(softwareUrl, pluginConfiguration);
        builds.addConnection(connection);
        configuration.addUrl(connectionConfiguration);
    }

    private PluginConfiguration createPluginConfigurationFrom(ConnectionConfiguration connectionConfiguration) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.put("login", connectionConfiguration.getLogin());
        pluginConfiguration.put("password", connectionConfiguration.getPassword());
        return pluginConfiguration;
    }

    private VisuwallPlugin findPluginCompatibleWith(ConnectionConfiguration connectionConfiguration) {
        PluginConfiguration pluginConfiguration = createPluginConfigurationFrom(connectionConfiguration);
        for (VisuwallPlugin plugin : plugins) {
            URL softwareUrl = connectionConfiguration.asUrl();
            if(plugin.accept(softwareUrl, pluginConfiguration)) {
                LOG.info("Plugin acceptation - "+plugin.getName()+" ... OK");
                return plugin;
            }
            LOG.info("Plugin acceptation - "+plugin.getName()+" ... KO");
        }
        return null;
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
}


