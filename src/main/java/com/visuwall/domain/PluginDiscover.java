package com.visuwall.domain;

import com.visuwall.api.plugin.VisuwallPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class PluginDiscover {

    private static final Logger LOG = LoggerFactory.getLogger(PluginDiscover.class);

    private Plugins plugins = new Plugins();

    public VisuwallPlugin findPluginCompatibleWith(Connection connection) {
        PluginConfiguration pluginConfiguration = Connection.createPluginConfigurationFrom(connection);
        for (VisuwallPlugin plugin : plugins) {
            URL softwareUrl = connection.asUrl();
            if(plugin.accept(softwareUrl, pluginConfiguration)) {
                LOG.info("Plugin acceptation - "+plugin.getName()+" ... OK");
                return plugin;
            }
            LOG.info("Plugin acceptation - "+plugin.getName()+" ... KO");
        }
        return null;
    }

}
