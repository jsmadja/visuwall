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

package com.visuwall.plugin.continuum;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ContinuumPlugin implements VisuwallPlugin<ContinuumConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(ContinuumPlugin.class);

    public ContinuumPlugin() {
        LOG.debug("Continuum plugin loaded.");
    }

    @Override
    public ContinuumConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        ContinuumConnection continuumConnection = new ContinuumConnection();
        continuumConnection.connect(url.toString(), "", "");
        return continuumConnection;
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<ContinuumConnection> getConnectionClass() {
        return ContinuumConnection.class;
    }

    @Override
    public String getName() {
        return "Continumm Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        if (isManageable(url)) {
            SoftwareId softwareId = new SoftwareId();
            softwareId.setName("Continuum");
            softwareId.setVersion("1.0");
            softwareId.setWarnings("");
            return softwareId;
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with Continuum");
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

    private boolean isManageable(URL url) {
        try {
            url = new URL(url.toString() + "/groupSummary.action");
            String content;
            content = new GenericSoftwareClient().download(url);
            return content.contains("Continuum");
        } catch (IOException e) {
            return false;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
