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

package com.visuwall.plugin.demo.track;

import com.visuwall.api.domain.*;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.TrackCapability;
import com.visuwall.plugin.demo.DemoConnection;
import com.visuwall.plugin.demo.SoftwareProjectIds;
import org.joda.time.DateMidnight;

import java.util.HashMap;
import java.util.Map;

import static com.visuwall.api.domain.Story.State.ACCEPTED;

public class DemoTrackConnection extends DemoConnection implements TrackCapability {

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        return new HashMap<SoftwareProjectId, String>(){{
            put(SoftwareProjectIds.earth, "Earth");
        }};
    }

    @Override
    public int getVelocity(SoftwareProjectId projectId) throws ProjectNotFoundException {
        return 5;
    }

    @Override
    public Iteration getCurrentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException {
        Stories stories = new Stories();
        stories.add(new Story.Builder().withState(ACCEPTED).withEstimation(3).build());
        DateMidnight end = DateMidnight.now().plusWeeks(2);
        return new Iteration(stories, end);
    }

    @Override
    public Backlog getBackLog(SoftwareProjectId projectId) throws ProjectNotFoundException {
        Stories stories = new Stories();
        stories.add(new Story.Builder().withState(Story.State.READY).withEstimation(7).build());
        return new Backlog(stories);
    }
}
