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

package com.visuwall.plugin.demo.build;

import com.visuwall.api.domain.*;
import com.visuwall.api.domain.build.BuildState;
import com.visuwall.api.domain.build.BuildTime;
import com.visuwall.api.domain.build.Commiter;
import com.visuwall.api.domain.build.TestResult;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.plugin.demo.DemoConnection;
import org.joda.time.DateTime;

import java.util.*;

import static com.visuwall.api.domain.build.BuildState.*;
import static com.visuwall.plugin.demo.SoftwareProjectIds.*;

public class DemoBuildConnection extends DemoConnection implements BuildCapability, TestCapability {

    private Map<SoftwareProjectId, BuildState> buildStates = new HashMap<SoftwareProjectId, BuildState>();

    private ChangeStateProject marsProj = new ChangeStateProject();

    public DemoBuildConnection() {
        buildStates.put(mars, FAILURE);
        buildStates.put(pluto, UNKNOWN);
        buildStates.put(uranus, SUCCESS);
        buildStates.put(neptune, SUCCESS);
        buildStates.put(saturn, UNSTABLE);
        buildStates.put(venus, UNSTABLE);
        buildStates.put(moon, SUCCESS);
        buildStates.put(earth, SUCCESS);
        buildStates.put(mercury, SUCCESS);
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        List<Commiter> commiters = new ArrayList<Commiter>();
        if (softwareProjectId.equals(mars)) {
            return marsProj.getCommiters(buildId);
        }
        return commiters;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        if (softwareProjectId.equals(mars)) {
            return marsProj.getBuildTime(buildId);
        }
        BuildTime buildTime = new BuildTime();
        int milisDuration = randomDuration();
        buildTime.setDuration(milisDuration);
        Date startDate = randomPastDate();
        buildTime.setStartTime(startDate);
        return buildTime;
    }

    private Date randomPastDate() {
        int minutesAgo = (int) (Math.random() * 50);
        return new DateTime().minusHours(minutesAgo).toDate();
    }

    private int randomDuration() {
        return (int) (Math.random() * 5000) * 60;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        BuildState buildState = buildStates.get(projectId);
        if (buildState == null) {
            throw new ProjectNotFoundException("Cannot find project for " + projectId);
        }
        if (mars.equals(projectId)) {
            return marsProj.getBuildState(buildId);
        }
        return buildState;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        if (projectId.equals(mars)) {
            return marsProj.isBuilding();
        }
        if (projectId.equals(moon)) {
            return true;
        }
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        String lastBuildId = "1";
        if (softwareProjectId.equals(mars)) {
            return marsProj.getLastBuildId();
        }
        return lastBuildId;
    }

    @Override
    public String toString() {
        return "Demo Build Connection";
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        TestResult testResult = new TestResult();
        if(projectId.equals(earth)) {
            testResult.setFailCount(3454);
            testResult.setPassCount(1245);
            testResult.setSkipCount(765);
        }
        return testResult;
    }
}
