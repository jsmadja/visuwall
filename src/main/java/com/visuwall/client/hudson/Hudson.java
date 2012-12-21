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

package com.visuwall.client.hudson;

import com.google.common.base.Preconditions;
import com.visuwall.client.common.Maven;
import com.visuwall.client.common.MavenIdNotFoundException;
import com.visuwall.client.hudson.domain.HudsonBuild;
import com.visuwall.client.hudson.domain.HudsonJob;
import com.visuwall.client.hudson.domain.HudsonTestResult;
import com.visuwall.client.hudson.exception.HudsonBuildNotFoundException;
import com.visuwall.client.hudson.exception.HudsonJobNotFoundException;
import com.visuwall.client.hudson.exception.HudsonViewNotFoundException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Hudson {

    private static final Logger LOG = LoggerFactory.getLogger(Hudson.class);

    private HudsonFinder hudsonFinder;

    private HudsonUrlBuilder hudsonUrlBuilder;

    private Maven maven = new Maven();

    private String url;

    public Hudson(String hudsonUrl) {
        this.url = hudsonUrl;
        hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        hudsonFinder = new HudsonFinder(hudsonUrlBuilder);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialize hudson with url " + hudsonUrl);
        }
    }

    public Hudson(String hudsonUrl, String login, String password) {
        this.url = hudsonUrl;
        hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        hudsonFinder = new HudsonFinder(hudsonUrlBuilder, login, password);
    }

    /**
     * @return List of all available projects on Hudson
     */
    public List<HudsonJob> findAllProjects() {
        List<HudsonJob> projects = new ArrayList<HudsonJob>();
        for (String jobName : hudsonFinder.findJobNames()) {
            try {
                HudsonJob hudsonProject = findJob(jobName);
                projects.add(hudsonProject);
            } catch (HudsonJobNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can't add project with name [" + jobName + "]. cause:" + e.getMessage());
                }
            }
        }
        return projects;
    }

    public List<String> findAllProjectNames() {
        List<String> projectNames = new ArrayList<String>();
        for (String jobName : hudsonFinder.findJobNames()) {
            projectNames.add(jobName);
        }
        return projectNames;
    }

    /**
     * @param jobName
     * @param buildNumber
     * @return HudsonBuild found in Hudson with its project name and build number
     * @throws HudsonBuildNotFoundException
     */
    public HudsonBuild findBuild(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        checkJobName(jobName);
        return hudsonFinder.find(jobName, buildNumber);
    }

    /**
     * @param jobName
     * @return HudsonJob found with its name
     * @throws HudsonJobNotFoundException
     */
    public HudsonJob findJob(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        return hudsonFinder.findJob(jobName);
    }

    /**
     * Return the description of the project identify by its projectName
     *
     * @param jobName
     * @return
     * @throws HudsonJobNotFoundException
     */
    public String getDescription(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        return hudsonFinder.getDescription(jobName);
    }

    /**
     * @param jobName
     * @return Date which we think the project will finish to build
     * @throws HudsonJobNotFoundException
     */
    public Date getEstimatedFinishTime(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        try {
            HudsonJob hudsonJob = hudsonFinder.findJob(jobName);
            HudsonBuild currentBuild = hudsonFinder.getCurrentBuild(jobName);
            if (currentBuild == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(jobName + " has no current build");
                }
                return new Date();
            }
            long averageBuildDurationTime = computeBuildDurationTime(hudsonJob);
            Date startTime = currentBuild.getStartTime();
            if (startTime == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(currentBuild + " has no start time");
                }
                return new Date();
            }
            long time = startTime.getTime();
            DateTime dateTime = new DateTime(time);
            DateTime estimatedFinishTime = dateTime.plus(averageBuildDurationTime);
            return estimatedFinishTime.toDate();
        } catch (HudsonBuildNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't find estimated finish time of job: " + jobName, e);
            }
        }
        return new Date();
    }

    public int getLastBuildNumber(String projectName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        checkJobName(projectName);
        return hudsonFinder.getLastBuildNumber(projectName);
    }

    public List<String> findJobNames() {
        return hudsonFinder.findJobNames();
    }

    public List<String> findViews() {
        return hudsonFinder.findViews();
    }

    public List<String> findJobNameByView(String viewName) throws HudsonViewNotFoundException {
        Preconditions.checkNotNull(viewName, "viewName is mandatory");
        return hudsonFinder.findJobNamesByView(viewName);
    }

    public String findMavenId(String jobName) throws MavenIdNotFoundException {
        String pomUrl = hudsonUrlBuilder.getPomUrl(jobName);
        return maven.findMavenIdFrom(pomUrl);
    }

    public List<Integer> getBuildNumbers(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        try {
            return hudsonFinder.getBuildNumbers(jobName);
        } catch (HudsonJobNotFoundException e) {
            throw new HudsonJobNotFoundException("Can't find build numbers of jobName '" + jobName + "'", e);
        }
    }

    private long computeBuildDurationTime(HudsonJob hudsonJob) throws HudsonJobNotFoundException {
        long averageTime;
        if (isNeverSuccessful(hudsonJob.getName())) {
            averageTime = maxDuration(hudsonJob);
        } else {
            averageTime = computeAverageBuildDuration(hudsonJob);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Average build time of " + hudsonJob.getName() + " is " + averageTime + " ms");
        }
        return averageTime;
    }

    private long maxDuration(HudsonJob hudsonProject) throws HudsonJobNotFoundException {
        long max = 0;
        List<Integer> buildNumbers = hudsonFinder.getBuildNumbers(hudsonProject.getName());

        for (int buildNumber : buildNumbers) {
            try {
                HudsonBuild build = findBuild(hudsonProject.getName(), buildNumber);
                max = Math.max(max, build.getDuration());
            } catch (HudsonBuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }

        return max;
    }

    private boolean isNeverSuccessful(String jobName) throws HudsonJobNotFoundException {
        List<Integer> buildNumbers = hudsonFinder.getBuildNumbers(jobName);
        for (int buildNumber : buildNumbers) {
            try {
                HudsonBuild build = findBuild(jobName, buildNumber);
                if (build.isSuccessful()) {
                    return false;
                }
            } catch (HudsonBuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        return true;
    }

    private long computeAverageBuildDuration(HudsonJob hudsonJob) throws HudsonJobNotFoundException {
        String projectName = hudsonJob.getName();
        float sumBuildDurationTime = 0;
        List<Integer> buildNumbers = hudsonFinder.getBuildNumbers(projectName);

        for (int buildNumber : buildNumbers) {
            try {
                HudsonBuild build = findBuild(projectName, buildNumber);
                if (build.isSuccessful()) {
                    sumBuildDurationTime += build.getDuration();
                }
            } catch (HudsonBuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }

        return (long) (sumBuildDurationTime / buildNumbers.size());
    }

    private void checkJobName(String jobName) {
        Preconditions.checkNotNull(jobName, "jobName is mandatory");
    }

    public HudsonTestResult findUnitTestResult(String jobName, int lastBuildNumber) throws HudsonBuildNotFoundException {
        return hudsonFinder.findUnitTestResult(jobName, lastBuildNumber);
    }

    public String getUrl() {
        return url;
    }

}
