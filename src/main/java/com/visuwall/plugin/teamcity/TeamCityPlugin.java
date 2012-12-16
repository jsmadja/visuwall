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

package com.visuwall.plugin.teamcity;

import java.net.URL;

import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.client.teamcity.resource.TeamCityServer;
import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;

import com.visuwall.domain.plugins.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class TeamCityPlugin implements VisuwallPlugin<TeamCityConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityPlugin.class);

    @VisibleForTesting
    GenericSoftwareClient genericSoftwareClient = new GenericSoftwareClient("guest", "");

    public TeamCityPlugin() {
        LOG.info("TeamCity plugin loaded.");
    }

    @Override
    public TeamCityConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        TeamCityConnection connectionPlugin = new TeamCityConnection();
        connectionPlugin.connect(url.toString(), pluginConfiguration.get("login"), pluginConfiguration.get("password"));
        return connectionPlugin;
    }

    @Override
    public Class<TeamCityConnection> getConnectionClass() {
        return TeamCityConnection.class;
    }

    @Override
    public String getName() {
        return "TeamCity plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        try {
            TeamCityServer teamCityServer = getServer(url.toString());
            return createSoftwareId(teamCityServer);
        } catch (ResourceNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with TeamCity", e);
        }
    }

    @Override
    public boolean accept(URL url, PluginConfiguration pluginConfiguration) {
        try {
            getSoftwareId(url, pluginConfiguration);
        } catch (SoftwareNotFoundException e) {
            return false;
        }
        return true;
    }

    private SoftwareId createSoftwareId(TeamCityServer teamCityServer) throws ResourceNotFoundException {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("TeamCity");
        String strVersion = getVersion(teamCityServer);
        softwareId.setVersion(strVersion);
        softwareId.setWarnings("");
        return softwareId;
    }

    private TeamCityServer getServer(String url) throws ResourceNotFoundException {
        String serverUrl = url + "/app/rest/server";
        return genericSoftwareClient.resource(serverUrl, TeamCityServer.class);
    }

    private String getVersion(TeamCityServer server) throws ResourceNotFoundException {
        return server.getVersionMajor() + "." + server.getVersionMinor();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", getName()) //
                .add("version", getVersion()).toString();
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.put("login", "guest");
        pluginConfiguration.put("password", "");
        return pluginConfiguration;
    }

}
