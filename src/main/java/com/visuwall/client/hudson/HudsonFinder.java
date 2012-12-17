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

import com.google.common.annotations.VisibleForTesting;
import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.client.hudson.domain.HudsonBuild;
import com.visuwall.client.hudson.domain.HudsonCommiter;
import com.visuwall.client.hudson.domain.HudsonJob;
import com.visuwall.client.hudson.domain.HudsonTestResult;
import com.visuwall.client.hudson.exception.HudsonBuildNotFoundException;
import com.visuwall.client.hudson.exception.HudsonJobNotFoundException;
import com.visuwall.client.hudson.exception.HudsonViewNotFoundException;
import com.visuwall.client.hudson.helper.HudsonXmlHelper;
import com.visuwall.client.hudson.resource.*;
import com.visuwall.client.hudson.resource.Hudson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class HudsonFinder {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonFinder.class);

    private HudsonUrlBuilder hudsonUrlBuilder;

    private GenericSoftwareClient client;

    private HudsonBuildBuilder hudsonBuildBuilder;

    private TestResultBuilder testResultBuilder;

    HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder) {
        this.client = new GenericSoftwareClient();
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonBuildBuilder = new HudsonBuildBuilder();
        this.testResultBuilder = new TestResultBuilder();
    }

    public HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder, String login, String password) {
        this.client = new GenericSoftwareClient(login, password);
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonBuildBuilder = new HudsonBuildBuilder();
        this.testResultBuilder = new TestResultBuilder();
    }

    HudsonBuild find(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        Build setBuild = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        String[] commiterNames = HudsonXmlHelper.getCommiterNames(setBuild);
        Set<HudsonCommiter> commiters = findCommiters(commiterNames);
        return hudsonBuildBuilder.createHudsonBuild(setBuild, commiters);
    }

    SurefireAggregatedReport findSurefireReport(String jobName, Build build) {
        String testResultUrl = hudsonUrlBuilder.getTestResultUrl(jobName, build.getNumber());
        try {
            return client.resource(testResultUrl, SurefireAggregatedReport.class);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    @VisibleForTesting
    Build findBuildByJobNameAndBuildNumber(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        String buildUrl = hudsonUrlBuilder.getBuildUrl(jobName, buildNumber);
        LOG.trace(buildUrl);
        Build setBuild;
        try {
            setBuild = client.resource(buildUrl, MavenModuleSetBuild.class);
        } catch (ResourceNotFoundException e) {
            try {
                setBuild = client.resource(buildUrl, FreeStyleBuild.class);
            } catch (ResourceNotFoundException e1) {
                throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for job " + jobName, e1);
            }
        }

        if (setBuild == null) {
            throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for job " + jobName);
        }
        return setBuild;
    }

    List<String> findJobNames() {
        List<String> jobNames = new ArrayList<String>();
        String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
        try {
            Hudson hudson = client.resource(projectsUrl, Hudson.class);
            for (Job job : hudson.getJobs()) {
                String name = job.getName();
                jobNames.add(name);
            }
        } catch (ResourceNotFoundException e) {
            LOG.trace(e.getMessage(), e);
        }
        return jobNames;
    }

    List<String> findJobNamesByView(String viewName) throws HudsonViewNotFoundException {
        try {
            List<String> jobNames = new ArrayList<String>();
            String viewUrl = hudsonUrlBuilder.getViewUrl(viewName);
            ListView view = client.resource(viewUrl, ListView.class);
            for (Job job : view.getJobs()) {
                jobNames.add(job.getName());
            }
            return jobNames;
        } catch (ResourceNotFoundException e) {
            throw new HudsonViewNotFoundException(e.getMessage(), e);
        }
    }

    List<String> findViews() {
        List<String> views = new ArrayList<String>();
        try {
            String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
            Hudson hudson = client.resource(projectsUrl, Hudson.class);
            for (View view : hudson.getViews()) {
                views.add(view.getName());
            }
        } catch (ResourceNotFoundException e) {
            LOG.trace(e.getMessage(), e);
        }
        return views;
    }

    String getDescription(String jobName) throws HudsonJobNotFoundException {
        Project moduleSet = findJobByName(jobName);
        return moduleSet.getDescription();
    }

    HudsonJob findJob(String projectName) throws HudsonJobNotFoundException {
        Project moduleSet = findJobByName(projectName);
        return createHudsonProjectFrom(moduleSet);
    }

    int getLastBuildNumber(String projectName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        Project job = findJobByName(projectName);
        Build lastBuild = job.getLastBuild();
        if (lastBuild == null) {
            throw new HudsonBuildNotFoundException("Project " + projectName + " has no last build");
        }
        return lastBuild.getNumber();
    }

    Set<HudsonCommiter> findCommiters(String[] commiterNames) {
        Set<HudsonCommiter> commiters = new TreeSet<HudsonCommiter>();
        for (String commiterName : commiterNames) {
            try {
                String url = hudsonUrlBuilder.getUserUrl(commiterName);
                HudsonUser hudsonUser = client.resource(url, HudsonUser.class);
                HudsonCommiter commiter = new HudsonCommiter(hudsonUser.getId());
                commiter.setName(commiterName);
                commiter.setEmail(hudsonUser.getEmail());
                commiters.add(commiter);
            } catch (ResourceNotFoundException e) {
                LOG.trace("Can't find user " + commiterName, e);
            }
        }
        return commiters;
    }

    private Project findJobByName(String jobName) throws HudsonJobNotFoundException {
        String jobUrl = hudsonUrlBuilder.getJobUrl(jobName);
        Project project = findProjectByName(jobName, jobUrl);
        if (project == null) {
            throw new HudsonJobNotFoundException("Can't find job with name '" + jobName + "'");
        }
        return project;
    }

    private Project findProjectByName(String jobName, String jobUrl) throws HudsonJobNotFoundException {
        Project project;
        try {
            project = client.resource(jobUrl, MavenModuleSet.class);
        } catch (ResourceNotFoundException e) {
            try {
                project = client.resource(jobUrl, FreeStyleProject.class);
            } catch (ResourceNotFoundException e1) {
                try {
                    project = client.resource(jobUrl, MatrixProject.class);
                } catch (ResourceNotFoundException e2) {
                    try {
                        project = client.resource(jobUrl, ExternalJob.class);
                    } catch (ResourceNotFoundException e3) {
                        throw new HudsonJobNotFoundException("Can't find job with name '" + jobName + "'", e3);
                    }
                }
            }
        }
        return project;
    }

    private HudsonJob createHudsonProjectFrom(Project project) {
        String name = project.getName();
        String description = project.getDescription();
        String color = project.getColor();
        boolean disabled = Color.DISABLED.value().equals(color) || Color.GREY.value().equals(color);

        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setName(name);
        hudsonJob.setDescription(description);
        hudsonJob.setDisabled(disabled);
        return hudsonJob;
    }

    List<Integer> getBuildNumbers(String jobName) throws HudsonJobNotFoundException {
        Project modelJob = findJobByName(jobName);
        List<Build> builds = modelJob.getBuilds();
        List<Integer> buildNumbers = new ArrayList<Integer>();
        for (Build build : builds) {
            int buildNumber = build.getNumber();
            buildNumbers.add(buildNumber);
        }
        Collections.sort(buildNumbers);
        return buildNumbers;
    }

    HudsonBuild getCurrentBuild(String jobName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        Project modelJob = findJobByName(jobName);
        Build currentHudsonRun = modelJob.getLastBuild();
        int currentBuildNumber = -1;
        if (currentHudsonRun != null) {
            currentBuildNumber = currentHudsonRun.getNumber();
        }
        HudsonBuild currentHudsonBuild = null;
        if (currentBuildNumber != -1) {
            currentHudsonBuild = find(jobName, currentBuildNumber);
        }
        return currentHudsonBuild;
    }

    public HudsonTestResult findUnitTestResult(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        Build build = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        SurefireAggregatedReport surefireReport = findSurefireReport(jobName, build);
        if (surefireReport != null) {
            return testResultBuilder.buildUnitTestResult(surefireReport);
        }
        return new HudsonTestResult();
    }

}
