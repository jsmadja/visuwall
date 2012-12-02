package com.visuwall;

import com.google.common.base.Function;
import net.awired.visuwall.api.domain.BuildState;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;
import net.awired.visuwall.plugin.jenkins.JenkinsPlugin;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Collections2.transform;

public class Wall {

    private static JenkinsConnection connection;
    private static Map<SoftwareProjectId,String> softwareProjectIdStringMap;

    private static Map<String, Project> projectsByBuildId = new ConcurrentHashMap<String, Project>();

    static {
        try {
            connection = new JenkinsPlugin().getConnection(new URL("http://ci.awired.net/jenkins/"), new HashMap<String, String>());
            softwareProjectIdStringMap = connection.listSoftwareProjectIds();
        }catch(Throwable t) {
            t.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        refreshAllProjects();
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }).start();
    }

    public List<Project> allProjects() {
        return new ArrayList<Project>(projectsByBuildId.values());
    }

    private static List<Project> refreshAllProjects() {
        Set<SoftwareProjectId> projects = softwareProjectIdStringMap.keySet();
        return new ArrayList<Project>(transform(projects, new Function<SoftwareProjectId, Project>() {
            @Override
            public Project apply(SoftwareProjectId softwareProjectId) {
            try {
                return buildProjectFrom(softwareProjectId);
            } catch (Exception e) {
                return new Project();
            }
            }
        }));
    }

    private static Project buildProjectFrom(SoftwareProjectId projectId) throws Exception {
        String lastBuildId = connection.getLastBuildId(projectId);
        if(projectHasChanged(projectId)) {
            Project build = refreshBuild(projectId, lastBuildId);
            projectsByBuildId.put(projectId.getProjectId() + lastBuildId, build);
            return build;
        }
        return projectsByBuildId.get(projectId.getProjectId() + lastBuildId);
    }

    private static Project refreshBuild(SoftwareProjectId projectId, String lastBuildId) throws ProjectNotFoundException, BuildNotFoundException {
        String name = connection.getName(projectId);
        BuildState buildState = connection.getBuildState(projectId, lastBuildId);
        BuildTime buildTime = connection.getBuildTime(projectId, lastBuildId);
        TestResult testResult = connection.analyzeUnitTests(projectId);
        long duration = buildTime.getDuration();
        Date startTime = buildTime.getStartTime();
        return Project.create().
                withName(name).
                withState(buildState).
                withDuration(duration).
                withLastBuildDate(startTime).
                withSuccessfulTestCount(testResult.getPassCount()).
                withFailedTestCount(testResult.getFailCount()).
                withSkippedTestCount(testResult.getSkipCount()).
                build();
    }

    private static boolean projectHasChanged(SoftwareProjectId projectId) throws Exception {
        String lastBuildId = connection.getLastBuildId(projectId);
        return !projectsByBuildId.containsKey(projectId.getProjectId() + lastBuildId);
    }

}


