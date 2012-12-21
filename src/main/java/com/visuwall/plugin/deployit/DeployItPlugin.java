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

package com.visuwall.plugin.deployit;

import com.google.common.base.Preconditions;
import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.GenericSoftwareClientFactory;
import com.visuwall.client.deployit.resource.RepositoryObjectIds;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class DeployItPlugin implements VisuwallPlugin<DeployItConnection> {

    private final static Logger LOG = LoggerFactory.getLogger(DeployItPlugin.class);

    private GenericSoftwareClient client;
    private GenericSoftwareClientFactory factory;

    public DeployItPlugin() {
        LOG.debug("DeployIt plugin loaded.");
        factory = new GenericSoftwareClientFactory();
    }

    @Override
    public DeployItConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        DeployItConnection connectionPlugin = new DeployItConnection();
        connectionPlugin.connect(url.toString(), pluginConfiguration.get("login"), pluginConfiguration.get("password"));
        return connectionPlugin;
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<DeployItConnection> getConnectionClass() {
        return DeployItConnection.class;
    }

    @Override
    public String getName() {
        return "DeployIt Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        client = factory.createClient(pluginConfiguration.getValues());
        if (isDeployIt(url.toString())) {
            return createSoftwareId();
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with DeployIt");
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

    @Override
    public boolean requiresPassword() {
        return false;
    }

    private SoftwareId createSoftwareId() {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("DeployIt");
        softwareId.setVersion("unknown");
        softwareId.setWarnings("");
        return softwareId;
    }

    private boolean isDeployIt(String url) {
        String serverUrl = url + "/deployit/query";
        return client.exist(serverUrl, RepositoryObjectIds.class);
    }
}
