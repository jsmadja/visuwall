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

package com.visuwall.client.pivotaltracker.resource;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "story")
@XmlAccessorType(XmlAccessType.FIELD)
public class Story implements Comparable<Story> {

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
