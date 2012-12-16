package com.visuwall.web;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.plugins.PluginConfiguration;
import com.visuwall.domain.plugins.PluginDiscover;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URL;

import static javax.ws.rs.core.Response.ok;

@Path("/connection")
@Consumes("*/*")
@Produces("application/json")
public class ConnectionResource {

    private PluginDiscover pluginDiscover = new PluginDiscover();

    @POST
    public Response getPlugin(Connection connection) throws Exception {
        VisuwallPlugin plugin = pluginDiscover.findPluginCompatibleWith(connection);
        if(plugin != null) {
            URL url = connection.asUrl();
            connection.setPluginName(plugin.getName());
            PluginConfiguration pluginConfiguration = Connection.createPluginConfigurationFrom(connection);
            SoftwareId softwareId = plugin.getSoftwareId(url, pluginConfiguration);
            connection.setWarning(softwareId.getWarnings());
            connection.setVisuwallConnection(plugin.getConnection(url, pluginConfiguration));
        }
        return ok().entity(connection).build();
    }

}
