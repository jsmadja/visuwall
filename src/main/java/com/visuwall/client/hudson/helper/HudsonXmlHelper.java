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

package com.visuwall.client.hudson.helper;

import com.google.common.base.Preconditions;
import com.visuwall.client.hudson.resource.Build;
import com.visuwall.client.hudson.resource.Culprit;

import java.util.List;

public class HudsonXmlHelper {

    private HudsonXmlHelper() {
    }

    public static boolean isSuccessful(Build build) {
        checkBuild(build);
        String state = build.getResult();
        return "SUCCESS".equals(state);
    }

    public static String[] getCommiterNames(Build build) {
        checkBuild(build);
        List<Culprit> users = build.getCulprits();
        String[] commiters = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            Culprit hudsonModelUser = users.get(i);
            String name = hudsonModelUser.getFullName();
            commiters[i] = name;
        }
        return commiters;
    }

    private static void checkBuild(Build build) {
        Preconditions.checkNotNull(build, "build is mandatory");
    }

}
