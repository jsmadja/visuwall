/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
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

package com.visuwall.client.hudson;

import com.visuwall.client.hudson.domain.HudsonBuild;
import com.visuwall.client.hudson.domain.HudsonCommiter;
import com.visuwall.client.hudson.helper.HudsonXmlHelper;
import com.visuwall.client.hudson.resource.Build;

import java.util.Date;
import java.util.Set;

class HudsonBuildBuilder {

    HudsonBuild createHudsonBuild(Build build, Set<HudsonCommiter> commiters) {
        HudsonBuild hudsonBuild = new HudsonBuild();
        hudsonBuild.setState(build.getResult());
        hudsonBuild.setDuration(build.getDuration());
        hudsonBuild.setStartTime(new Date(build.getTimestamp()));
        hudsonBuild.setSuccessful(HudsonXmlHelper.isSuccessful(build));
        hudsonBuild.setCommiters(commiters);
        hudsonBuild.setBuildNumber(build.getNumber());
        return hudsonBuild;
    }

}
