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

package com.visuwall.plugin.pivotaltracker;

import com.visuwall.api.domain.BuildTime;
import com.visuwall.client.pivotaltracker.resource.Iteration;

import java.util.Date;

public class BuildTimer {

    private Iteration iteration;

    public BuildTimer(Iteration iteration) {
        this.iteration = iteration;
    }

    public BuildTime build() {
        BuildTime buildTime = new BuildTime();
        buildTime.setDuration(computeRemainingTime());
        return buildTime;
    }

    private long computeRemainingTime() {
        long now = new Date().getTime();
        Date finish = iteration.getFinish();
        long finishTime = finish.getTime();
        return finishTime - now;
    }

}
