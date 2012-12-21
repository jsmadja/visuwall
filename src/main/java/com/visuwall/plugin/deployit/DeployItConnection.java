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

package com.visuwall.plugin.deployit;

import com.visuwall.api.domain.*;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.exception.ViewNotFoundException;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.api.plugin.capability.ViewCapability;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.client.deployit.DeployIt;
import com.visuwall.client.deployit.resource.ArchivedTasks;
import com.visuwall.client.deployit.resource.Step;
import com.visuwall.client.deployit.resource.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DeployItConnection implements BuildCapability, TestCapability, ViewCapability {

    private DeployIt deployIt;

    private final static Logger LOG = LoggerFactory.getLogger(DeployItConnection.class);
    private String url;

    @Override
    public void connect(String url, String login, String password) {
        deployIt = new DeployIt(url, login, password);
        this.url = url;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        try {
            Task task = getTask(projectId);
            String environment = task.getEnvironment();
            String application = task.getApplication();
            Integer version = task.getVersion();
            return environment + "<br/>" + application + " v" + version;
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find name for " + projectId, e);
        }
    }

    private Task getTask(SoftwareProjectId projectId) throws ResourceNotFoundException {
        List<Task> tasks = findAllTasks();
        for (Task task : tasks) {
            String environment = task.getEnvironment();
            String application = task.getApplication();
            if (projectId.getProjectId().equals(environment + " - " + application)) {
                return task;
            }
        }
        return null;
    }

    private Task getTask(SoftwareProjectId projectId, String expectedVersion) throws ResourceNotFoundException {
        List<Task> tasks = findAllTasks();
        for (Task task : tasks) {
            String environment = task.getEnvironment();
            String application = task.getApplication();
            Integer version = task.getVersion();
            if (expectedVersion.equals(version.toString())
                    && projectId.getProjectId().equals(environment + " - " + application)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>();
        try {
            List<Task> tasks = findAllTasks();
            for (Task task : tasks) {
                String environment = task.getEnvironment();
                String application = task.getApplication();
                String projectId = environment + " - " + application;
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(projectId);
                softwareProjectIds.put(softwareProjectId, projectId);
            }
        } catch (ResourceNotFoundException e) {
            LOG.error("Cannot get archived tasks", e);
        }
        return softwareProjectIds;
    }

    private List<Task> findAllTasks() throws ResourceNotFoundException {
        ArchivedTasks archivedTasks = deployIt.getArchivedTasks();
        List<Task> tasks = archivedTasks.getTasks();
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t2.getCompletionDate().compareTo(t1.getCompletionDate());
            }
        });
        return tasks;
    }

    private List<Task> getTasks(SoftwareProjectId softwareProjectId) throws ResourceNotFoundException {
        List<Task> filteredTasks = new ArrayList<Task>();
        List<Task> tasks = findAllTasks();
        for (Task task : tasks) {
            String environment = task.getEnvironment();
            String application = task.getApplication();
            if (softwareProjectId.getProjectId().equals(environment + " - " + application)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        List<Commiter> commiters = new ArrayList<Commiter>();
        try {
            Task task = getTask(softwareProjectId, buildId);
            if (task == null) {
                throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                        + buildId);
            }
            Commiter commiter = new Commiter(task.getUser());
            commiter.setName(task.getUser());
            commiters.add(commiter);
        } catch (ResourceNotFoundException e) {
            throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                    + buildId, e);
        }
        return commiters;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        try {
            Task task = getTask(softwareProjectId, buildId);
            if (task == null) {
                throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                        + buildId);
            }
            BuildTime buildTime = new BuildTime();
            buildTime.setStartTime(task.getStartDate());
            long duration = task.getCompletionDate().getTime() - task.getStartDate().getTime();
            if (duration < 1000) {
                duration = 1000;
            }
            buildTime.setDuration(duration);
            return buildTime;
        } catch (ResourceNotFoundException e) {
            throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                    + buildId);
        }
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return BuildState.SUCCESS;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        Task task;
        try {
            task = getTask(softwareProjectId);
            if (task == null) {
                throw new ProjectNotFoundException("Cannot find deployment for " + softwareProjectId);
            }
            Integer version = task.getVersion();
            return version.toString();
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find deployment for " + softwareProjectId);
        }
    }

    @Override
    public String toString() {
        return "DeployIt Connection";
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        TestResult testResult = new TestResult();
        try {
            Task task = getTask(projectId);
            List<Step> steps = task.getSteps();
            for (Step step : steps) {
                switch (step.getState()) {
                    case SKIPPED:
                        testResult.setSkipCount(testResult.getSkipCount() + 1);
                        break;
                    case DONE:
                        if (step.getFailureCount() == 0) {
                            testResult.setPassCount(testResult.getPassCount() + 1);
                        } else {
                            testResult.setFailCount(testResult.getFailCount() + 1);
                        }
                        break;
                }
            }
        } catch (ResourceNotFoundException e) {
            LOG.warn("Cannot retrieve steps for " + projectId, e);
        }
        return testResult;
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        for (String environmentName : views) {
            try {
                List<String> deployedApplications = deployIt.getDeployedApplicationsByEnvironment(environmentName);
                for (String deployedApplication : deployedApplications) {
                    SoftwareProjectId softwareProjectId = createSoftwareProjectId(deployedApplication, environmentName);
                    softwareProjectIds.add(softwareProjectId);
                }
            } catch (ResourceNotFoundException e) {
                LOG.warn("Cannot retrieve deployed applications on environment " + environmentName, e);
            }
        }
        return softwareProjectIds;
    }

    private SoftwareProjectId createSoftwareProjectId(String deployedApplication, String environment) {
        String projectId = deployedApplication.replaceFirst(environment + "/", environment + " - ");
        return new SoftwareProjectId(projectId);
    }

    @Override
    public List<String> findViews() {
        List<String> views = new ArrayList<String>();
        try {
            views = deployIt.getEnvironmentNames();
        } catch (ResourceNotFoundException e) {
            LOG.warn("Cannot retrieve views", e);
        }
        return views;
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        try {
            return deployIt.getDeployedApplicationsByEnvironment(viewName);
        } catch (ResourceNotFoundException e) {
            throw new ViewNotFoundException("Cannot retrieve project names for view " + viewName, e);
        }
    }

}
