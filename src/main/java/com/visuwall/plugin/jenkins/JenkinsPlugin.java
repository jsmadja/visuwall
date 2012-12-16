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

package com.visuwall.plugin.jenkins;

import java.net.MalformedURLException;
import java.net.URL;

import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.GenericSoftwareClientFactory;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;

import com.visuwall.domain.plugins.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class JenkinsPlugin implements VisuwallPlugin<JenkinsConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsPlugin.class);

    private GenericSoftwareClientFactory factory;

    public JenkinsPlugin() {
        LOG.info("Jenkins plugin loaded.");
        factory = new GenericSoftwareClientFactory();
    }

    @Override
    public JenkinsConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        JenkinsConnection jenkinsConnectionPlugin = new JenkinsConnection();
        String login = pluginConfiguration.get("login");
        String password = pluginConfiguration.get("password");
        jenkinsConnectionPlugin.connect(url.toString(), login, password);
        return jenkinsConnectionPlugin;
    }

    @Override
    public Class<JenkinsConnection> getConnectionClass() {
        return JenkinsConnection.class;
    }

    @Override
    public String getName() {
        return "Jenkins plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");

        if (pluginConfiguration == null) {
            pluginConfiguration = getDefaultPluginConfiguration();
        }
        try {
            GenericSoftwareClient client = factory.createClient(pluginConfiguration.getValues());
            URL apiUrl = new URL(url.toString() + "/api/");
            String xml = client.download(apiUrl);
            JenkinsVersionPage jenkinsApiPage = new JenkinsVersionPage(xml);
            if (jenkinsApiPage.isJenkinsApiPage()) {
                return jenkinsApiPage.createSoftwareId();
            }
        } catch (ResourceNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Jenkins", e);
        } catch (MalformedURLException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Jenkins", e);
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with Jenkins");
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
