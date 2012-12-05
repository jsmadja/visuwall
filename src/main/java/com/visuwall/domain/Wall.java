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

    public Wall() {
        start();
    }

    public void addConnection(URL url) {
        LOG.info("Trying to identify a compatible plugin for url:" + url.toString());
        for (VisuwallPlugin plugin : plugins) {
            PluginConfiguration pluginConfiguration = PluginConfiguration.noConfiguration;
            if(plugin.accept(url, pluginConfiguration)) {
                LOG.info(plugin.getName() + " is compatible with url:" + url.toString());
                BasicCapability connection = plugin.getConnection(url, pluginConfiguration);
                builds.addConnection(connection);
                configuration.addUrl(url);
            }
        }
    }

    private void start() {
        new Thread(this).start();
    }

    public Builds getBuilds() {
        return builds;
    }

    @Override
    public void run() {
        while (true) {
            try {
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


