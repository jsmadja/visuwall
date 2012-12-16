package com.visuwall.client.pivotaltracker.resource;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stories")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stories implements Iterable<Story> {

    @XmlElements({ @XmlElement(name = "story") })
    private Set<Story> stories = new TreeSet<Story>();

    public Story get(int i) {
        return (Story) stories.toArray()[i];
    }

    public void add(Story story) {
        this.stories.add(story);
    }

    public Collection<Story> all() {
        return Collections.unmodifiableCollection(stories);
    }

    @Override
    public Iterator<Story> iterator() {
        return stories.iterator();
    }

    public int getEstimatedPoints() {
        int estimatedPoints = 0;
        for (Story story : stories) {
            if(story.isEstimated()) {
                estimatedPoints += story.getEstimate();
            }
        }
        return estimatedPoints;
    }

    public boolean contains(Story story) {
        return stories.contains(story);
    }
}
