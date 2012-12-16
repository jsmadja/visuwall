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

import com.visuwall.api.domain.*;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.exception.ViewNotFoundException;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.api.plugin.capability.ViewCapability;
import com.visuwall.client.hudson.Hudson;
import com.visuwall.client.hudson.domain.HudsonBuild;
import com.visuwall.client.hudson.domain.HudsonCommiter;
import com.visuwall.client.hudson.domain.HudsonJob;
import com.visuwall.client.hudson.domain.HudsonTestResult;
import com.visuwall.client.hudson.exception.HudsonBuildNotFoundException;
import com.visuwall.client.hudson.exception.HudsonJobNotFoundException;
import com.visuwall.client.hudson.exception.HudsonViewNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.visuwall.plugin.jenkins.States.asVisuwallState;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNumeric;

public final class JenkinsConnection implements BuildCapability, ViewCapability, TestCapability {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsConnection.class);

    private Hudson hudson;

    private boolean connected;

    private static final Collection<String> DEFAULT_VIEWS = Arrays.asList("Alle", "Todo", "Tous", "\u3059\u3079\u3066",
            "Tudo", "\u0412\u0441\u0435", "Hepsi", "All");

    private String url;

    @Override
    public void connect(String url, String login, String password) {
        checkNotNull(url, "url is mandatory");
        if (isBlank(url)) {
            throw new IllegalStateException("url can't be null.");
        }
        this.url = url;
        if (StringUtils.isBlank(login)) {
            hudson = new Hudson(url);
        } else {
            hudson = new Hudson(url, login, password);
        }
        connected = true;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        checkConnected();
        Map<SoftwareProjectId, String> projectIds = new HashMap<SoftwareProjectId, String>();
        List<String> names = hudson.findAllProjectNames();
        for (String name : names) {
            SoftwareProjectId projectId = new SoftwareProjectId(name);
            projectIds.put(projectId, name);
        }
        return projectIds;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkSoftwareProjectId(projectId);
        checkConnected();
        if (!isNumeric(buildId)) {
            return false;
        }

        try {
            String projectName = jobName(projectId);
            HudsonBuild build = hudson.findBuild(projectName, Integer.valueOf(buildId));
            if (build.getState() == null) {
                return true;
            }
            if (build.getDuration() == 0) {
                return true;
            }
            return false;
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (NumberFormatException e) {
            throw new BuildNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkSoftwareProjectId(projectId);
        checkBuildId(buildId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            HudsonBuild hudsonBuild = hudson.findBuild(projectName, Integer.valueOf(buildId));
            String hudsonState = hudsonBuild.getState();
            if (hudsonState == null) {
                return BuildState.UNKNOWN;
            }
            return asVisuwallState(hudsonState);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    @Override
    public String getLastBuildId(SoftwareProjectId projectId) throws ProjectNotFoundException, BuildIdNotFoundException {
        checkSoftwareProjectId(projectId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            return String.valueOf(hudson.getLastBuildNumber(projectName));
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildIdNotFoundException(e);
        }
    }

    @Override
    public List<String> findViews() {
        checkConnected();
        List<String> views = hudson.findViews();
        views.removeAll(DEFAULT_VIEWS);
        return views;
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        checkConnected();
        checkNotNull(viewName, "viewName is mandatory");
        try {
            return hudson.findJobNameByView(viewName);
        } catch (HudsonViewNotFoundException e) {
            throw new ViewNotFoundException("can't find view named: " + viewName, e);
        }
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        checkConnected();
        checkNotNull(views, "views is mandatory");
        Set<SoftwareProjectId> projectIds = new HashSet<SoftwareProjectId>();
        for (String viewName : views) {
            try {
                List<String> projectNames = hudson.findJobNameByView(viewName);
                projectIds.addAll(findSoftwareProjectIdsByNames(projectNames));
            } catch (HudsonViewNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
        return new ArrayList<SoftwareProjectId>(projectIds);
    }

    private List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        checkNotNull(names, "names is mandatory");
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        for (String name : names) {
            SoftwareProjectId projectId = new SoftwareProjectId(name);
            projectIds.add(projectId);
        }
        return projectIds;
    }

    @Override
    public String getDescription(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        try {
            String jobName = jobName(projectId);
            return hudson.getDescription(jobName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't find description of project id: " + projectId, e);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        String projectName = softwareProjectId.getProjectId();
        try {
            HudsonJob project = hudson.findJob(projectName);
            return project.getName();
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't get name of project " + softwareProjectId, e);
        }
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            String jobName = softwareProjectId.getProjectId();
            HudsonBuild hudsonBuild = hudson.findBuild(jobName, Integer.valueOf(buildId));
            BuildTime buildTime = new BuildTime();
            buildTime.setDuration(hudsonBuild.getDuration());
            buildTime.setStartTime(hudsonBuild.getStartTime());
            return buildTime;
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildId + " of project " + softwareProjectId, e);
        }
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String jobName = softwareProjectId.getProjectId();
            HudsonJob job = hudson.findJob(jobName);
            return job.isDisabled();
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't find job with software project id: " + softwareProjectId, e);
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
        List<Commiter> commiters = new ArrayList<Commiter>();
        try {
            String jobName = softwareProjectId.getProjectId();
            HudsonBuild build = hudson.findBuild(jobName, Integer.valueOf(buildId));
            Set<HudsonCommiter> commiterSet = build.getCommiters();
            for (HudsonCommiter hudsonCommiter : commiterSet) {
                Commiter commiter = new Commiter(hudsonCommiter.getId());
                commiter.setName(hudsonCommiter.getName());
                commiter.setEmail(hudsonCommiter.getEmail());
                if (!commiters.contains(commiter)) {
                    commiters.add(commiter);
                }
            }
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build with software project id: " + softwareProjectId
                    + " and buildId: " + buildId, e);
        }

        return commiters;
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId softwareProjectId) {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String jobName = softwareProjectId.getProjectId();
            int lastBuildId = hudson.getLastBuildNumber(jobName);
            HudsonTestResult unitTestResult = hudson.findUnitTestResult(jobName, lastBuildId);
            return TestResults.createFrom(unitTestResult);
        } catch (HudsonJobNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't analyze integration test of project: " + softwareProjectId, e);
            }
            return new TestResult();
        } catch (HudsonBuildNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't analyze integration test of project: " + softwareProjectId, e);
            }
            return new TestResult();
        }
    }

    private String jobName(SoftwareProjectId softwareProjectId) throws HudsonJobNotFoundException {
        String jobName = softwareProjectId.getProjectId();
        if (jobName == null) {
            throw new HudsonJobNotFoundException("Project id " + softwareProjectId + " does not contain id");
        }
        return jobName;
    }

    private void checkBuildId(String buildId) {
        checkNotNull(buildId, "buildId is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        checkConnected();
        checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    private void checkConnected() {
        checkState(connected, "You must connect your plugin");
    }

}
