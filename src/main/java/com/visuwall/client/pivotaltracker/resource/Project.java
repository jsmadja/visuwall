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

import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Collections2.filter;
import static com.visuwall.client.pivotaltracker.resource.CustomPredicates.*;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class Project {

    private static final Logger LOG = LoggerFactory.getLogger(Project.class);

    private Integer id;

    private String name;

    @XmlElement(name = "iteration_length")
    private Integer iterationLength;

    @XmlElement(name = "last_activity_at")
    private String lastActivityAt;

    @XmlElement(name = "week_start_day")
    private String weekStartDay;

    @XmlElement(name = "first_iteration_start_time")
    private String firstIterationStartTime;

    @XmlElement(name = "current_iteration_number")
    private Integer currentIterationNumber;

    @XmlElement(name = "current_velocity")
    private Integer currentVelocity;

    @XmlTransient
    private Stories stories = new Stories();

    @XmlTransient
    private Iteration currentIteration;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getLastActivityAt() {
        if (lastActivityAt == null) {
            LOG.warn("Project " + getName() + " has no last activity date");
            return new DateMidnight().toDate();
        }
        try {
            return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(lastActivityAt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid format " + lastActivityAt, e);
        }
    }

    public Integer getIterationLength() {
        return iterationLength;
    }

    public String getWeekStartDay() {
        return weekStartDay;
    }

    public void setFirstIterationStartTime(String firstIterationStartTime) {
        this.firstIterationStartTime = firstIterationStartTime;
    }

    public Date getFirstIterationStartTime() {
        try {
            return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(firstIterationStartTime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid format " + firstIterationStartTime, e);
        }
    }

    public void setIterationLength(Integer iterationLength) {
        this.iterationLength = iterationLength;
    }

    public void setWeekStartDay(String weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    public void setLastActivityAt(String lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public Integer getCurrentIterationNumber() {
        return currentIterationNumber;
    }

    public void setCurrentIterationNumber(Integer currentIterationNumber) {
        this.currentIterationNumber = currentIterationNumber;
    }

    public int getCurrentVelocity() {
        return currentVelocity;
    }

    public void setCurrentVelocity(Integer currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    public float getNbSprints() {
        Collection<Story> estimatedStories = getEstimatedStories();
        if (estimatedStories.isEmpty()) {
            return 0;
        }
        return (float) getEstimatedPoints() / (float) currentVelocity;
    }

    private Collection<Story> getEstimatedStories() {
        Collection<Story> all = stories.all();
        return filter(all, and(isFeature, isReady, isEstimated));
    }

    public void setStories(Stories stories) {
        this.stories = stories;
    }

    public int getEstimatedPoints() {
        int totalPoints = 0;
        Collection<Story> estimatedStories = getEstimatedStories();
        for (Story story : estimatedStories) {
            totalPoints += story.getEstimate();
        }
        return totalPoints;
    }

    public void setCurrentIteration(Iteration iteration) {
        this.currentIteration = iteration;
    }

    public Iteration getCurrentIteration() {
        return currentIteration;
    }

}
