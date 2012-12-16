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

package com.visuwall.plugin.bamboo;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class BambooPlugin implements VisuwallPlugin<BambooConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(BambooPlugin.class);

    public BambooPlugin() {
        LOG.info("Bamboo plugin loaded.");
    }

    @Override
    public BambooConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        BambooConnection connection = new BambooConnection();
        String login = "";
        String password = "";
        if (pluginConfiguration.hasValueFor("login") && pluginConfiguration.hasValueFor("password")) {
            login = pluginConfiguration.get("login");
            password = pluginConfiguration.get("password");
        }
        connection.connect(url.toString(), login, password);
        return connection;
    }

    @Override
    public Class<BambooConnection> getConnectionClass() {
        return BambooConnection.class;
    }

    @Override
    public String getName() {
        return "Bamboo Plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        try {
            SoftwareId softwareId = new SoftwareId();
            softwareId.setName("Bamboo");
            softwareId.setVersion(BambooVersionExtractor.extractVersion(url));
            return softwareId;
        } catch (BambooVersionNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Jenkins");
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", getName()) //
                .add("version", getVersion()).toString();
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }
}
