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
import java.util.*;

@XmlRootElement(name = "stories")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stories implements Iterable<Story> {

    @XmlElements({@XmlElement(name = "story")})
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
            if (story.isEstimated()) {
                estimatedPoints += story.getEstimate();
            }
        }
        return estimatedPoints;
    }

    public boolean contains(Story story) {
        return stories.contains(story);
    }
}
