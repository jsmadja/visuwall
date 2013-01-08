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

package com.visuwall.domain.builds;


import com.ocpsoft.pretty.time.PrettyTime;
import com.visuwall.api.domain.build.BuildState;
import com.visuwall.api.domain.build.BuildTime;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.build.TestResult;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.domain.Refreshable;
import com.visuwall.formatter.DurationFormatter;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;
import java.util.Locale;

import static com.visuwall.api.domain.build.BuildState.BUILDING;
import static com.visuwall.api.domain.build.BuildState.UNKNOWN;

public class Build implements Refreshable<Build> {

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
    private boolean removable;

    Build() {}

    public Build(BasicCapability connection, SoftwareProjectId projectId) {
        this.connection = connection;
        this.projectId = projectId;
        this.removable = false;
    }

    public BuildState getStatus() {
        return status;
    }

    @Override
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
    public int compareTo(Build build) {
        return this.name.compareToIgnoreCase(build.name);
    }

    @Override
    public void refresh() {
        try {
            refreshInfos();
            refreshTimes();
            if (connection instanceof TestCapability) {
                refreshTests();
            }
        } catch (ProjectNotFoundException e) {
            setRemoveable();
        } catch (BuildIdNotFoundException e) {
            status = UNKNOWN;
        } catch (BuildNotFoundException e) {
            status = UNKNOWN;
        }
    }

    private void refreshInfos() throws ProjectNotFoundException, BuildIdNotFoundException, BuildNotFoundException {
        BuildCapability buildCapability = (BuildCapability) connection;
        name = buildCapability.getName(projectId);
        String lastBuildId = buildCapability.getLastBuildId(projectId);
        if (buildCapability.isBuilding(projectId, lastBuildId)) {
            status = BUILDING;
        } else {
            status = buildCapability.getBuildState(projectId, lastBuildId);
        }
    }

    private void refreshTimes() throws ProjectNotFoundException, BuildIdNotFoundException, BuildNotFoundException {
        BuildCapability buildCapability = (BuildCapability) connection;
        String lastBuildId = buildCapability.getLastBuildId(projectId);
        BuildTime buildTime = buildCapability.getBuildTime(projectId, lastBuildId);
        lastBuildDate = buildTime.getStartTime();
        if (buildCapability.isBuilding(projectId, lastBuildId)) {
            duration = 0;
        } else {
            duration = buildTime.getDuration();
        }
    }

    private void refreshTests() {
        TestResult testResult = ((TestCapability) connection).analyzeUnitTests(projectId);
        successfulTestCount = testResult.getPassCount();
        failedTestCount = testResult.getFailCount();
        skippedTestCount = testResult.getSkipCount();
    }

    @JsonIgnore
    @Override
    public boolean isRefreshable() {
        try {
            return status == BUILDING || status == UNKNOWN || isCurrentlyBuilding() || newBuildIsAvailable();
        } catch (ProjectNotFoundException e) {
            setRemoveable();
            return false;
        } catch (BuildNotFoundException e) {
            return false;
        } catch (BuildIdNotFoundException e) {
            return false;
        }
    }

    private boolean isCurrentlyBuilding() throws ProjectNotFoundException, BuildNotFoundException {
        BuildCapability buildCapability = (BuildCapability) connection;
        return buildCapability.isBuilding(projectId, lastBuildId);
    }

    private boolean newBuildIsAvailable() throws ProjectNotFoundException, BuildIdNotFoundException {
        BuildCapability buildCapability = (BuildCapability) connection;
        String oldBuildId = lastBuildId;
        lastBuildId = buildCapability.getLastBuildId(projectId);
        return oldBuildId == null || !oldBuildId.equals(lastBuildId);
    }

    public boolean is(SoftwareProjectId projectId) {
        return this.projectId.equals(projectId);
    }

    public boolean hasName(String name) {
        return this.name != null && this.name.equalsIgnoreCase(name);
    }

    @JsonIgnore
    public boolean isBuilding() {
        return status == BUILDING;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setRemoveable() {
        this.removable = true;
    }

    @JsonIgnore
    public boolean isRemovable() {
        return removable;
    }

    @JsonIgnore
    public boolean isLinkedTo(String url) {
        return this.connection.getUrl().equalsIgnoreCase(url);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Build) {
            Build b = (Build) o;
            return hasName(b.name);
        }
        return false;
    }

    public Date getNativeLastBuildDate() {
        return lastBuildDate;
    }
}
