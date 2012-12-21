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

package com.visuwall.plugin.pivotaltracker;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.domain.plugins.PluginConfiguration;

import java.net.URL;

public class PivotalTrackerPlugin implements VisuwallPlugin<PivotalTrackerConnection> {

    @Override
    public PivotalTrackerConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        String login = pluginConfiguration.get("login");
        String password = pluginConfiguration.get("password");
        PivotalTrackerConnection pivotalTrackerConnection = new PivotalTrackerConnection();
        pivotalTrackerConnection.connect(url.toString(), login, password);
        return pivotalTrackerConnection;
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<PivotalTrackerConnection> getConnectionClass() {
        return PivotalTrackerConnection.class;
    }

    @Override
    public String getName() {
        return "PivotalTracker plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        GenericSoftwareClient genericSoftwareClient = new GenericSoftwareClient();
        try {
            String download = genericSoftwareClient.download(url);
            if (!download.contains("Pivotal Tracker")) {
                throw new SoftwareNotFoundException("Url " + url + " is not compatible with Pivotal Tracker");
            }
        } catch (ResourceNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Pivotal Tracker", e);
        }
        SoftwareId softwareId = new SoftwareId();
        softwareId.setCompatible(true);
        softwareId.setName("PivotalTracker");
        return softwareId;
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
        return true;
    }

}
