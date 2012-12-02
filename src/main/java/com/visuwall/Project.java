package com.visuwall;


import com.ocpsoft.pretty.time.PrettyTime;
import net.awired.visuwall.api.domain.BuildState;

import java.util.Date;
import java.util.Locale;

public class Project implements Comparable<Project>{

    private BuildState status = BuildState.UNKNOWN;
    private String name;
    private long duration;
    private Date lastBuildDate;
    private int successfulTestCount;
    private int failedTestCount;
    private int skippedTestCount;

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

    public static Builder create() {
        return new Builder();
    }

    @Override
    public int compareTo(Project project2) {
        if (this.status == project2.status) {
            return project2.lastBuildDate.compareTo(this.lastBuildDate);
        }
        return project2.status.compareTo(this.status);
    }

    public static class Builder {

        private String name;
        private BuildState buildState;
        private long duration;
        private Date lastBuildDate;
        private int successfulTestCount;
        private int skippedTestCount;
        private int failedTestCount;


        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.name = name;
            project.status = buildState;
            project.duration = duration;
            project.lastBuildDate = lastBuildDate;
            project.skippedTestCount = skippedTestCount;
            project.failedTestCount = failedTestCount;
            project.successfulTestCount = successfulTestCount;
            return project;
        }

        public Builder withState(BuildState buildState) {
            this.buildState = buildState;
            return this;
        }

        public Builder withDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder withLastBuildDate(Date lastBuildDate) {
            this.lastBuildDate = lastBuildDate;
            return this;
        }

        public Builder withSuccessfulTestCount(int successfulTestCount) {
            this.successfulTestCount = successfulTestCount;
            return this;
        }

        public Builder withFailedTestCount(int failedTestCount) {
            this.failedTestCount = failedTestCount;
            return this;
        }

        public Builder withSkippedTestCount(int skippedTestCount) {
            this.skippedTestCount = skippedTestCount;
            return this;
        }

    }
}
