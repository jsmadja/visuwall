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

package com.visuwall.api.plugin.capability;

import com.visuwall.api.domain.ProjectKey;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ConnectionException;
import com.visuwall.api.exception.MavenIdNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;

import java.util.Map;

public interface BasicCapability {

    /**
     * Initiate connection to the software
     * 
     * @param url
     * @param login
     * @param password
     * @throws ConnectionException
     */
    void connect(String url, String login, String password) throws ConnectionException;

    /**
     * Return the description of the project
     * 
     * @param softwareProjectId
     * @return
     */
    String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException;

    /**
     * Return the name of the project
     * 
     * @param projectId
     * @return
     */
    String getName(SoftwareProjectId projectId) throws ProjectNotFoundException;

    /**
     * Return the full list of project id contained in the software with there display name
     * 
     * @return
     */
    Map<SoftwareProjectId, String> listSoftwareProjectIds();

    /**
     * Returns true if project is disabled in the software
     * 
     * @param softwareProjectId
     * @return
     * @throws ProjectNotFoundException
     */
    boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException;

    String getUrl();

}
