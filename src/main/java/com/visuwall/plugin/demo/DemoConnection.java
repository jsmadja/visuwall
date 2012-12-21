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

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;

import java.util.HashMap;
import java.util.Map;

import static com.visuwall.plugin.demo.SoftwareProjectIds.*;
import static com.visuwall.plugin.demo.SoftwareProjectIds.mercury;
import static com.visuwall.plugin.demo.SoftwareProjectIds.venus;

public class DemoConnection implements BasicCapability {

    private Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>() {{
        put(moon, "Moon");
        put(earth, "Earth");
        put(mars, "Mars");
        put(pluto, "Pluto");
        put(neptune, "Neptune");
        put(uranus, "Uranus");
        put(saturn, "Saturn");
        put(mercury, "Mercury");
        put(venus, "Venus");
    }};

    private String url;

    @Override
    public void connect(String url, String login, String password) {
        this.url = url;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        return softwareProjectIds;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        String name = projectId.getProjectId();
        String firstLetter = "" + name.charAt(0);
        return firstLetter.toUpperCase() + name.substring(1);
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public String getUrl() {
        return url;
    }

}
