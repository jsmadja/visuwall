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

package com.visuwall.api.plugin;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.plugins.PluginConfiguration;

import java.net.URL;

public interface VisuwallPlugin<T extends BasicCapability> {

    T getConnection(URL url, PluginConfiguration pluginConfiguration);

    /** login, password, ... */
    PluginConfiguration getDefaultPluginConfiguration();

    Class<T> getConnectionClass();

    float getVersion();

    String getName();

    SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException;

    boolean accept(URL url, PluginConfiguration pluginConfiguration);

    boolean requiresPassword();
}
