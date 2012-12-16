package com.visuwall.client.pivotaltracker.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "story")
@XmlAccessorType(XmlAccessType.FIELD)
public class Story implements Comparable<Story>{

    private Integer id;

    private String name;

    @XmlElement(name = "story_type")
    private StoryType storyType;

    @XmlElement(name = "current_state")
    private CurrentState currentState;

    private Integer estimate;

    @XmlTransient
    private Project project;

    public Story() {
    }

    public Story(StoryType storyType, CurrentState currentState, int estimate) {
        this.storyType = storyType;
        this.currentState = currentState;
        this.estimate = estimate;
    }

    public Integer getId() {
        return id;
    }

    public StoryType getStoryType() {
        return storyType;
    }

    public CurrentState getCurrentState() {
        return currentState;
    }

    public Integer getEstimate() {
        return estimate;
    }

    @Override
    public String toString() {
        String storyTypeName = storyType == null ? "" : storyType.name();
        String currentStateName = currentState == null ? "" : currentState.name();
        return id + ".[" + storyTypeName + "][" + currentStateName + "] " + name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public int compareTo(Story story) {
        return id.compareTo(story.id);
    }

    public boolean isEstimated() {
        return estimate != null;
    }
}
