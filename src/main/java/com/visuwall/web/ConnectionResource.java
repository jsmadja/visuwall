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

package com.visuwall.web;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.plugins.PluginConfiguration;
import com.visuwall.domain.plugins.PluginDiscover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URL;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/connection")
@Consumes("*/*")
@Produces("application/json")
public class ConnectionResource {

    private PluginDiscover pluginDiscover = new PluginDiscover();

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionResource.class);

    @POST
    public Response getPlugin(Connection connection) throws Exception {
        VisuwallPlugin plugin = pluginDiscover.findPluginCompatibleWith(connection);
        if (plugin != null) {
            URL url = connection.asUrl();
            connection.setPluginName(plugin.getName());
            PluginConfiguration pluginConfiguration = Connection.createPluginConfigurationFrom(connection);
            SoftwareId softwareId = plugin.getSoftwareId(url, pluginConfiguration);
            connection.setWarning(softwareId.getWarnings());
            connection.setSoftwareInfo(softwareId.getName()+" "+softwareId.getVersion());
            if (plugin.requiresPassword() && !pluginConfiguration.hasPassword()) {
                return status(UNAUTHORIZED).build();
            }
            try {
                BasicCapability capability = plugin.getConnection(url, pluginConfiguration);
                connection.setVisuwallConnection(capability);
            } catch (Throwable t) {
                LOG.info(t.getMessage());
                return status(UNAUTHORIZED).build();
            }
        }
        return ok().entity(connection).build();
    }

}
