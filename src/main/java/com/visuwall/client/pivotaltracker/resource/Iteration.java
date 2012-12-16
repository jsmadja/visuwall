package com.visuwall.client.pivotaltracker.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Collections2.filter;
import static com.visuwall.client.pivotaltracker.resource.CustomPredicates.isEstimated;
import static com.visuwall.client.pivotaltracker.resource.CustomPredicates.isFeature;
import static com.visuwall.client.pivotaltracker.resource.CustomPredicates.isReady;

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
