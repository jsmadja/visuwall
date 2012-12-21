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

package com.visuwall.client.bamboo;

public class Bamboo23UrlBuilder {

    private static final String API_REST = "/api/rest";
    private String url;

    public Bamboo23UrlBuilder(String url) {
        this.url = url;
    }

    public String getLoginUrl(String login, String password) {
        return url + API_REST + "/login.action?username=" + login + "&password=" + password;
    }

    public String getBuildNamesUrl(String auth) {
        return url + API_REST + "/listBuildNames.action?" + auth(auth);
    }

    public String getLatestBuildResultsForProject(String auth, String projectKey) {
        return url + API_REST + "/getLatestBuildResultsForProject.action?" + auth(auth) + "&" + projectKey(projectKey);
    }

    public String getRecentlyCompletedBuildResultsForProject(String auth, String projectKey) {
        return url + API_REST + "/getRecentlyCompletedBuildResultsForProject.action?" + auth(auth) + "&"
                + projectKey(projectKey);
    }

    private String projectKey(String projectKey) {
        return "projectKey=" + projectKey;
    }

    private String auth(String auth) {
        return "auth=" + auth;
    }

}
