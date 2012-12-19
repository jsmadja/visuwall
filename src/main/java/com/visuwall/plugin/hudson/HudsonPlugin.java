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

package com.visuwall.plugin.hudson;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.GenericSoftwareClientFactory;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class HudsonPlugin implements VisuwallPlugin<HudsonConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonPlugin.class);

    private GenericSoftwareClientFactory factory;

    public HudsonPlugin() {
        LOG.debug("Hudson plugin loaded.");
        factory = new GenericSoftwareClientFactory();
    }

    @Override
    public HudsonConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        HudsonConnection hudsonConnectionPlugin = new HudsonConnection();
        String login = pluginConfiguration.get("login");
        String password = pluginConfiguration.get("password");
        hudsonConnectionPlugin.connect(url.toString(), login, password);
        return hudsonConnectionPlugin;
    }

    @Override
    public Class<HudsonConnection> getConnectionClass() {
        return HudsonConnection.class;
    }

    @Override
    public String getName() {
        return "Hudson plugin";
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
            if (isManageable(xml)) {
                return createSoftwareId(xml);
            }
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Hudson, content: " + xml);
        } catch (MalformedURLException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Hudson", e);
        } catch (ResourceNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Hudson", e);
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
    public boolean requiresPassword() {
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", getName())
                .toString();
    }

    private SoftwareId createSoftwareId(String xml) {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("Hudson");
        String strVersion = getVersion(xml);
        softwareId.setVersion(strVersion);
        return softwareId;
    }

    private String getVersion(String xml) {
        return new HudsonVersionExtractor(xml).version();
    }

    private boolean isManageable(String xml) {
        return xml.contains("Remote API [Hudson]");
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

}
