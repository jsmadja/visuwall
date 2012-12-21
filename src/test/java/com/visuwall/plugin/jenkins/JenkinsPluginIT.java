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

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.junit.Test;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class JenkinsPluginIT {

    @Test
    public void test_cloudbees() throws Exception {
        PluginConfiguration configuration = new PluginConfiguration();
        configuration.put("login", "jsmadja@xebia.fr");
        configuration.put("password", "xedy4bsa");
        URL url = new URL("https://jsmadja.ci.cloudbees.com");
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        SoftwareId softwareId = jenkinsPlugin.getSoftwareId(url, configuration);
        System.err.println(softwareId);
        JenkinsConnection connection = jenkinsPlugin.getConnection(url, configuration);
        Map<SoftwareProjectId,String> softwareProjectIdStringMap = connection.listSoftwareProjectIds();
        Collection<String> values = softwareProjectIdStringMap.values();
        for (String value : values) {
            System.err.println(value);
        }
    }

}
