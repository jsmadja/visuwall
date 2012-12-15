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

package com.visuwall.plugin.bamboo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visuwall.client.bamboo.Bamboo;
import com.visuwall.client.bamboo.exception.BambooBuildNotFoundException;
import com.visuwall.client.bamboo.exception.BambooBuildNumberNotFoundException;
import com.visuwall.client.bamboo.exception.BambooEstimatedFinishTimeNotFoundException;
import com.visuwall.client.bamboo.exception.BambooPlanNotFoundException;
import com.visuwall.client.bamboo.exception.BambooStateNotFoundException;
import com.visuwall.client.bamboo.resource.Plan;
import com.visuwall.client.bamboo.resource.Result;
import com.visuwall.api.domain.BuildState;
import com.visuwall.api.domain.BuildTime;
import com.visuwall.api.domain.Commiter;
import com.visuwall.api.domain.ProjectKey;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.TestResult;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.MavenIdNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.TestCapability;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class BambooConnection implements BuildCapability, TestCapability {

    @VisibleForTesting
    Bamboo bamboo;

    private boolean connected;

    private String url;

    private static final Logger LOG = LoggerFactory.getLogger(BambooConnection.class);

    @Override
    public void connect(String url, String login, String password) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url can't be null.");
        }
        this.url = url;
        if (StringUtils.isNotBlank(login)) {
            bamboo = new Bamboo(url, login, password);
        } else {
            bamboo = new Bamboo(url);
        }
        connected = true;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId softwareProjectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            String projectName = getProjectKey(softwareProjectId);
            return bamboo.isBuilding(projectName, Integer.valueOf(buildId));
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project with software project id:" + softwareProjectId, e);
        }
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildId(buildId);
        try {
            String projectName = getProjectKey(projectId);
            String bambooState = bamboo.getState(projectName);
            return States.asVisuwallState(bambooState);
        } catch (BambooStateNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public String getLastBuildId(SoftwareProjectId projectId) throws ProjectNotFoundException, BuildIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        try {
            String id = getProjectKey(projectId);
            return String.valueOf(bamboo.getLastResultNumber(id));
        } catch (BambooBuildNumberNotFoundException e) {
            throw new BuildIdNotFoundException(e);
        }
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return "";
    }

    private String getProjectKey(SoftwareProjectId projectId) {
        return projectId.getProjectId();
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        checkConnected();
        Map<SoftwareProjectId, String> projects = new HashMap<SoftwareProjectId, String>();
        List<Plan> plans = bamboo.findAllPlans();
        for (Plan plan : plans) {
            String key = plan.getKey();
            SoftwareProjectId softwareProjectId = new SoftwareProjectId(key);
            projects.put(softwareProjectId, plan.getName());
        }
        return projects;
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectKey = softwareProjectId.getProjectId();
            Plan plan = bamboo.findPlan(projectKey);
            String name = plan.getProjectName();
            return name;
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException("Can't find name of software project id: " + softwareProjectId);
        }
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            String projectKey = softwareProjectId.getProjectId();
            Result bambooResult = bamboo.findResult(projectKey, Integer.valueOf(buildId));
            BuildTime buildTime = new BuildTime();
            buildTime.setDuration(bambooResult.getBuildDuration());
            buildTime.setStartTime(bambooResult.getBuildStartedTime());
            return buildTime;
        } catch (BambooBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildId + " of project " + softwareProjectId, e);
        }
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        String planKey = softwareProjectId.getProjectId();
        try {
            Plan plan = bamboo.findPlan(planKey);
            return !plan.isEnabled();
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException("Can't find plan with software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        return new ArrayList<Commiter>();
    }

    private void checkBuildId(String buildId) {
        Preconditions.checkNotNull(buildId, "buildId is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        checkConnected();
        TestResult result = new TestResult();
        try {
            String planKey = projectId.getProjectId();
            int buildId = bamboo.getLastResultNumber(planKey);
            Result findResult = bamboo.findResult(planKey, buildId);
            int successfulTestCount = findResult.getSuccessfulTestCount();
            int failedTestCount = findResult.getFailedTestCount();
            result.setFailCount(failedTestCount);
            result.setPassCount(successfulTestCount);
        } catch (BambooBuildNumberNotFoundException e) {
            LOG.warn("Can't analyze unit tests for projectId:" + projectId, e);
        } catch (BambooBuildNotFoundException e) {
            LOG.warn("Can't analyze unit tests for projectId:" + projectId, e);
        }
        return result;
    }

}
