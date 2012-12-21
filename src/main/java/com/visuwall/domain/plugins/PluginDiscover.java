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
            LOG.debug("Plugin acceptation - " + plugin.getName() + " ... KO");
        }
        return null;
    }

}
