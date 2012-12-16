package com.visuwall.domain.tracks;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.TrackCapability;
import com.visuwall.domain.Refreshable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateMidnight;

public class Track implements Comparable<Track>, Refreshable {

    private String name;

    @JsonIgnore
    private BasicCapability connection;

    @JsonIgnore
    private TrackCapability trackCapability;

    @JsonIgnore
    private SoftwareProjectId projectId;

    @JsonIgnore
    private boolean removeable;

    private int estimatedPointsInFuture;
    private int velocity;
    private int remainingPointsInCurrentIteration;
    private int remainingDays;

    public Track(BasicCapability connection, SoftwareProjectId projectId) {
        this.connection = connection;
        this.trackCapability = (TrackCapability) connection;
        this.projectId = projectId;
        this.removeable = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Track track) {
        if(this.name == null || track.name == null) {
            return 0;
        }
        return this.name.compareToIgnoreCase(track.name);
    }

    @Override
    public void refresh() {
        try {
            refreshInfos();
        } catch (ProjectNotFoundException e) {
            setRemoveable();
        }
    }

    private void refreshInfos() throws ProjectNotFoundException {
        this.name = connection.getName(projectId);
        this.estimatedPointsInFuture = trackCapability.getEstimatedPointsInFuture(projectId);
        this.velocity = trackCapability.getVelocity(projectId);
        this.remainingPointsInCurrentIteration = trackCapability.getRemainingPointsInCurrentIteration(projectId);
        this.remainingDays = trackCapability.getEndOfCurrentIteration(projectId).minus(DateMidnight.now().getMillis()).getDayOfYear();
    }

    @JsonIgnore
    @Override
    public boolean isRefreshable() {
        try {
            return newBuildIsAvailable();
        } catch (ProjectNotFoundException e) {
            setRemoveable();
            return false;
        }
    }

    private boolean newBuildIsAvailable() throws ProjectNotFoundException {
        return true;
    }

    public boolean is(SoftwareProjectId projectId) {
        return this.projectId.equals(projectId);
    }

    public boolean hasName(String name) {
        return this.name != null && this.name.equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public void setRemoveable() {
        this.removeable = true;
    }

    @JsonIgnore
    public boolean isRemoveable() {
        return removeable;
    }

    @JsonIgnore
    public boolean isLinkedTo(String url) {
        return this.connection.getUrl().equalsIgnoreCase(url);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Track) {
            Track t = (Track) o;
            return hasName(t.name);
        }
        return false;
    }

    public int getEstimatedPointsInFuture() {
        return estimatedPointsInFuture;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getRemainingPointsInCurrentIteration() {
        return remainingPointsInCurrentIteration;
    }

    public int getRemainingDays() {
        return remainingDays;
    }
}