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

package com.visuwall.plugin.sonar;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;

import java.net.URL;

import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;

import com.visuwall.domain.plugins.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

public class SonarPlugin implements VisuwallPlugin<SonarConnection> {

    private static final double SONAR_MINIMUM_COMPATIBLE_VERSION = 2.4;

    static final String SONAR_CORE_VERSION_KEY = "sonar.core.version";

    private static final Logger LOG = LoggerFactory.getLogger(SonarPlugin.class);

    private GenericSoftwareClient client;

    private SonarConnectionFactory sonarConnectionFactory;

    private SonarDetector sonarDetector = new SonarDetector();

    private SonarVersionExtractor sonarVersionExtractor = new SonarVersionExtractor();

    private SonarCompatibleVersionChecker sonarCompatibleVersionChecker = new SonarCompatibleVersionChecker(
            SONAR_MINIMUM_COMPATIBLE_VERSION);

    public SonarPlugin() {
        client = new GenericSoftwareClient();
        sonarConnectionFactory = new SonarConnectionFactory();
        LOG.info("Sonar plugin loaded.");
    }

    @Override
    public SonarConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        checkNotNull(url, "url is mandatory");
        String login = pluginConfiguration.get("login");
        String password = pluginConfiguration.get("password");
        return sonarConnectionFactory.create(url.toString(), login, password);
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<SonarConnection> getConnectionClass() {
        return SonarConnection.class;
    }

    @Override
    public String getName() {
        return "Sonar plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        checkNotNull(url, "url is mandatory");
        if (sonarDetector.isSonarPropertiesPage(url)) {
            return createSoftwareIdFromProperties(url);
        } else if (sonarDetector.isSonarWelcomePage(url)) {
            return createSoftwareIdFromWelcomePage(url);
        }
        throw new SoftwareNotFoundException("Url " + url.toString() + " is not compatible with Sonar");
    }

    @Override
    public boolean accept(URL url, PluginConfiguration pluginConfiguration) {
        try {
            getSoftwareId(url, pluginConfiguration);
        } catch(SoftwareNotFoundException e) {
            return false;
        }
        return true;
    }

    private SoftwareId createSoftwareIdFromWelcomePage(URL url) {
        String version = sonarVersionExtractor.welcomePageVersion(url);
        return createSoftwareId(version);
    }

    private SoftwareId createSoftwareIdFromProperties(URL url) throws SoftwareNotFoundException {
        String propertiesUrl = sonarDetector.buildPropertiesUrl(url);
        Properties properties = client.existingResource(propertiesUrl, Properties.class, APPLICATION_XML_TYPE);
        String version = sonarVersionExtractor.propertiesVersion(properties);
        return createSoftwareId(version);
    }

    private SoftwareId createSoftwareId(String version) {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("Sonar");
        boolean versionIsCompatible = sonarCompatibleVersionChecker.versionIsCompatible(version);
        softwareId.setCompatible(versionIsCompatible);
        softwareId.setVersion(version);
        if (!versionIsCompatible) {
            softwareId.setWarnings("Sonar version " + version
                    + " is not compatible with Visuwall. Please use a version >= " + SONAR_MINIMUM_COMPATIBLE_VERSION);
        }
        return softwareId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", getName()) //
                .add("version", getVersion())//
                .toString();
    }


}
