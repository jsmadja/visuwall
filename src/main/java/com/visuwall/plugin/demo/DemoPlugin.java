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
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.plugins.PluginConfiguration;

import java.net.URL;

public abstract class DemoPlugin <T extends BasicCapability> implements VisuwallPlugin<T>  {

    @Override
    public T getConnection(URL url, PluginConfiguration pluginConfiguration) {
        T connection = getConnection();
        connection.connect(url.toString(), null, null);
        return connection;
    }

    protected abstract T getConnection();

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<T> getConnectionClass() {
        return (Class<T>) getConnection().getClass();
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        if (url == null || !getExpectedUrl().equals(url.toString())) {
            throw new SoftwareNotFoundException(getName() + " is not compatible with url : " + url);
        }
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName(getName());
        softwareId.setCompatible(true);
        softwareId.setVersion("1.0");
        softwareId.setWarnings("This is a demo plugin");
        return softwareId;
    }

    @Override
    public boolean accept(URL url, PluginConfiguration pluginConfiguration) {
        return url != null && getExpectedUrl().equals(url.toString());
    }

    protected abstract String getExpectedUrl();

    @Override
    public boolean requiresPassword() {
        return false;
    }

}
