package com.visuwall.domain.plugins;

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.domain.connections.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PluginDiscover {

    private static final Logger LOG = LoggerFactory.getLogger(PluginDiscover.class);

    private Plugins plugins = new Plugins();

    private Map<String, VisuwallPlugin> pluginsByUrl = new HashMap<String, VisuwallPlugin>();

    public VisuwallPlugin findPluginCompatibleWith(Connection connection) {
        String connectionUrl = connection.getUrl();
        LOG.info("Looking for a compatible plugin with "+connectionUrl);
        if (pluginsByUrl.containsKey(connectionUrl)) {
            return pluginsByUrl.get(connectionUrl);
        }
        PluginConfiguration pluginConfiguration = Connection.createPluginConfigurationFrom(connection);
        for (VisuwallPlugin plugin : plugins) {
            URL softwareUrl = connection.asUrl();
            if (plugin.accept(softwareUrl, pluginConfiguration)) {
                LOG.info("Plugin acceptation - " + plugin.getName() + " ... OK");
                pluginsByUrl.put(connectionUrl, plugin);
                return plugin;
            }
            LOG.info("Plugin acceptation - " + plugin.getName() + " ... KO");
        }
        return null;
    }

}
