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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Collections2.filter;
import static com.visuwall.client.pivotaltracker.resource.CustomPredicates.*;

@XmlRootElement(name = "iteration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Iteration {

    private String finish;

    public Date getFinish() {
        try {
            return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(finish);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid format " + finish, e);
        }
    }

    @XmlElement(name = "team_strength")
    private Float teamStrength;

    private Stories stories;

    public Float getTeamStrength() {
        return teamStrength;
    }

    public Stories getStories() {
        return stories;
    }

    public void setStories(Stories stories) {
        this.stories = stories;
    }

    public int getRemainingPoints() {
        Collection<Story> all = stories.all();
        int points = 0;
        Collection<Story> filter = filter(all, and(isFeature, isReady, isEstimated));
        for (Story story : filter) {
            points += story.getEstimate();
        }
        return points;
    }

    public boolean contains(Story story) {
        return stories.contains(story);
    }
}
