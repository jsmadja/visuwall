package com.visuwall.plugin.demo;

import com.visuwall.api.domain.*;
import com.visuwall.api.domain.quality.QualityMeasure;
import com.visuwall.api.domain.quality.QualityMetric;
import com.visuwall.api.domain.quality.QualityResult;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.exception.ViewNotFoundException;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.MetricCapability;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.api.plugin.capability.ViewCapability;
import org.joda.time.DateTime;

import java.util.*;

import static com.visuwall.api.domain.BuildState.*;
import static com.visuwall.plugin.demo.SoftwareProjectIds.*;

public class DemoConnection implements BuildCapability {

    private Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>();
    private Map<SoftwareProjectId, BuildState> buildStates = new HashMap<SoftwareProjectId, BuildState>();

    private ChangeStateProject marsProj = new ChangeStateProject();

    private String url = "http://demo.visuwall.ci";

    public DemoConnection() {
        softwareProjectIds.put(moon, "Moon");
        softwareProjectIds.put(earth, "Earth");
        softwareProjectIds.put(mars, "Mars");
        softwareProjectIds.put(pluto, "Pluto");
        softwareProjectIds.put(neptune, "Neptune");
        softwareProjectIds.put(uranus, "Uranus");
        softwareProjectIds.put(saturn, "Saturn");
        softwareProjectIds.put(mercury, "Mercury");
        softwareProjectIds.put(venus, "Venus");

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
    public void connect(String url, String login, String password) {
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        return softwareProjectIds;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        String name = projectId.getProjectId();
        String firstLetter = "" + name.charAt(0);
        return firstLetter.toUpperCase() + name.substring(1);
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
        return "Demo Connection";
    }

}
