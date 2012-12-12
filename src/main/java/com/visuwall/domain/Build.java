package com.visuwall.domain;


import com.ocpsoft.pretty.time.PrettyTime;
import com.visuwall.api.domain.BuildState;
import com.visuwall.api.domain.BuildTime;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.TestResult;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.formatter.DurationFormatter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Locale;

import static com.visuwall.api.domain.BuildState.BUILDING;
import static com.visuwall.api.domain.BuildState.UNKNOWN;

public class Build implements Comparable<Build>{

    private static final Logger LOG = LoggerFactory.getLogger(Build.class);

    private BuildState status = BuildState.UNKNOWN;
    private String name;
    private long duration;
    private Date lastBuildDate;
    private int successfulTestCount;
    private int failedTestCount;
    private int skippedTestCount;

    @JsonIgnore
    private BasicCapability connection;
    @JsonIgnore
    private SoftwareProjectId projectId;
    @JsonIgnore
    private String lastBuildId;
    @JsonIgnore
    private boolean removeable;

    public Build(BasicCapability connection, SoftwareProjectId projectId) {
        this.connection = connection;
        this.projectId = projectId;
        this.removeable = false;
    }

    public BuildState getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return new DurationFormatter(duration).toString();
    }

    public String getLastBuildDate() {
        Locale.setDefault(Locale.ROOT);
        return new PrettyTime().format(lastBuildDate);
    }

    public int getSuccessfulTestCount() {
        return successfulTestCount;
    }

    public int getFailedTestCount() {
        return failedTestCount;
    }

    public int getSkippedTestCount() {
        return skippedTestCount;
    }

    @Override
    public int compareTo(Build build2) {
        if(build2 == null) {
            return -1;
        }
        if (this.status == build2.status) {
            if(build2.lastBuildDate == null) {
                return -1;
            }
            return build2.lastBuildDate.compareTo(this.lastBuildDate);
        }
        if(build2.status == null  || this.status == null) {
            return -1;
        }
        return build2.status.ordinal() - this.status.ordinal();
    }

    public void refresh() throws Exception {
        refreshInfos();
        refreshTimes();
        refreshTests();
    }

    private void refreshInfos() {
        try {
            BuildCapability buildCapability = (BuildCapability) connection;
            String lastBuildId = buildCapability.getLastBuildId(projectId);
            name = buildCapability.getName(projectId);
            if(buildCapability.isBuilding(projectId, lastBuildId)) {
                status = BUILDING;
            } else {
                status = buildCapability.getBuildState(projectId, lastBuildId);
            }
        } catch(ProjectNotFoundException e) {
            setRemoveable();
        } catch(BuildIdNotFoundException e) {
            status = UNKNOWN;
        } catch(BuildNotFoundException e) {
            status = UNKNOWN;
        }
    }

    private void refreshTimes() throws Exception {
        BuildCapability buildCapability = (BuildCapability) connection;
        String lastBuildId = buildCapability.getLastBuildId(projectId);
        BuildTime buildTime = buildCapability.getBuildTime(projectId, lastBuildId);
        lastBuildDate = buildTime.getStartTime();
        if(buildCapability.isBuilding(projectId, lastBuildId)) {
            duration = 0;
        } else {
            duration = buildTime.getDuration();
        }
    }

    private void refreshTests() {
        TestResult testResult = ((TestCapability)connection).analyzeUnitTests(projectId);
        successfulTestCount = testResult.getPassCount();
        failedTestCount = testResult.getFailCount();
        skippedTestCount = testResult.getSkipCount();
    }

    public boolean isRefreshable() throws Throwable {
        return isCurrentlyBuilding() || newBuildIsAvailable();
    }

    private boolean isCurrentlyBuilding() throws ProjectNotFoundException, BuildNotFoundException {
        BuildCapability buildCapability = (BuildCapability) connection;
        return buildCapability.isBuilding(projectId, lastBuildId);
    }

    private boolean newBuildIsAvailable() throws ProjectNotFoundException, BuildIdNotFoundException {
        BuildCapability buildCapability = (BuildCapability) connection;
        String oldBuildId = lastBuildId;
        lastBuildId = buildCapability.getLastBuildId(projectId);
        if(oldBuildId == null) {
            return true;
        }
        return !oldBuildId.equals(lastBuildId);
    }

    public boolean is(SoftwareProjectId projectId) {
        return this.projectId.equals(projectId);
    }

    public boolean hasName(String name) {
        if(this.name == null) {
            return false;
        }
        return this.name.equalsIgnoreCase(name);
    }

    public boolean isBuilding() {
        return status == BUILDING;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setRemoveable() {
        this.removeable = true;
    }

    public boolean isRemoveable() {
        return removeable;
    }
}
