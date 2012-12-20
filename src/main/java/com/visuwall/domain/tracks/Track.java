package com.visuwall.domain.tracks;

import com.visuwall.api.domain.Backlog;
import com.visuwall.api.domain.Iteration;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.Stories;
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
    private boolean removable;

    private int actualVelocity;
    private int daysToGo;
    private int acceptedStories;
    private int storiesInProgress;
    private int availableStories;
    private int numberOfSprints;
    private int remainingStories;
    private int storiesInValidation;
    private int scheduledStories;
    private int waitingForEstimationStories;
    private int availableStoryPoints;

    Track() {}

    public Track(BasicCapability connection, SoftwareProjectId projectId) {
        this.connection = connection;
        this.trackCapability = (TrackCapability) connection;
        this.projectId = projectId;
        this.name = projectId.getProjectId();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Track track) {
        if (this.name == null || track.name == null) {
            return 0;
        }
        return this.name.compareToIgnoreCase(track.name);
    }

    @Override
    public void refresh() {
        try {
            refreshInfos();
        } catch (ProjectNotFoundException e) {
            setRemovable();
        }
    }

    private void refreshInfos() throws ProjectNotFoundException {
        Iteration currentIteration = trackCapability.getCurrentIteration(projectId);

        this.name = connection.getName(projectId);
        this.daysToGo = currentIteration.getEnd().minus(DateMidnight.now().getMillis()).getDayOfYear();
        Stories stories = currentIteration.getStories();
        this.scheduledStories = stories.scheduledOnly().count();
        this.acceptedStories = stories.acceptedOnly().count();
        this.storiesInProgress = stories.startedOnly().count();
        this.remainingStories = stories.remainingOnly().count();
        this.storiesInValidation = stories.inValidationOnly().count();
        this.waitingForEstimationStories = stories.waitingForEstimationOnly().count();

        Backlog backlog = trackCapability.getBackLog(projectId);
        Stories storiesInBacklog = backlog.getStories();
        this.availableStories = storiesInBacklog.count();
        this.actualVelocity = trackCapability.getVelocity(projectId);
        this.waitingForEstimationStories = storiesInBacklog.waitingForEstimationOnly().count();
        this.numberOfSprints = (int) ((double) storiesInBacklog.getEstimation() / (double)actualVelocity);
        this.availableStoryPoints = storiesInBacklog.getEstimation();
    }

    @JsonIgnore
    @Override
    public boolean isRefreshable() {
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

    public void setRemovable() {
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
        if (o instanceof Track) {
            Track t = (Track) o;
            return hasName(t.name);
        }
        return false;
    }

    public int getActualVelocity() {
        return actualVelocity;
    }

    public int getDaysToGo() {
        return daysToGo;
    }

    public int getAcceptedStories() {
        return acceptedStories;
    }

    public int getStoriesInProgress() {
        return storiesInProgress;
    }

    public int getStoriesInValidation() {
        return storiesInValidation;
    }

    public int getAvailableStories() {
        return availableStories;
    }

    public int getNumberOfSprints() {
        return numberOfSprints;
    }

    public int getRemainingStories() {
        return remainingStories;
    }

    public int getScheduledStories() {
        return scheduledStories;
    }

    public int getWaitingForEstimationStories() {
        return waitingForEstimationStories;
    }
}