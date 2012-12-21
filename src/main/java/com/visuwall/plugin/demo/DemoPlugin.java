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

package com.visuwall.plugin.demo;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.domain.plugins.PluginConfiguration;

import java.net.URL;

public class DemoPlugin implements VisuwallPlugin<DemoConnection> {

    private static final String DEMO_VISUWALL_CI = "http://demo.visuwall.ci";

    @Override
    public DemoConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        DemoConnection connection = new DemoConnection();
        connection.connect(url.toString(), null, null);
        return connection;
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<DemoConnection> getConnectionClass() {
        return DemoConnection.class;
    }

    @Override
    public String getName() {
        return "Demo Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        if (url == null || !DEMO_VISUWALL_CI.equals(url.toString())) {
            throw new SoftwareNotFoundException(getName() + " is not compatible with url : " + url);
        }
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("demo");
        softwareId.setCompatible(true);
        softwareId.setVersion("1.0");
        softwareId.setWarnings("This is a demo plugin");
        return softwareId;
    }

    @Override
    public boolean accept(URL url, PluginConfiguration pluginConfiguration) {
        return url != null && DEMO_VISUWALL_CI.equals(url.toString());
    }

    @Override
    public boolean requiresPassword() {
        return false;
    }

}
