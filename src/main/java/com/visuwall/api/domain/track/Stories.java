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

package com.visuwall.api.domain.track;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.visuwall.api.domain.track.Story.State.*;

public class Stories {

    private List<Story> stories = new ArrayList<Story>();

    public Stories(Collection<Story> stories) {
        this.stories.addAll(stories);
    }

    public Stories() {

    }

    public int count() {
        return stories.size();
    }

    public Stories acceptedOnly() {
        return new Stories(filter(stories, new Predicate<Story>() {
            @Override
            public boolean apply(Story story) {
                return story.hasState(ACCEPTED);
            }
        }));
    }

    public Stories remainingOnly() {
        return new Stories(filter(stories, new Predicate<Story>() {
            @Override
            public boolean apply(Story story) {
                return story.hasState(READY);
            }
        }));
    }

    public Stories startedOnly() {
        return new Stories(filter(stories, new Predicate<Story>() {
            @Override
            public boolean apply(Story story) {
                return story.hasState(STARTED);
            }
        }));
    }

    public void add(Story story) {
        this.stories.add(story);
    }

    public Stories inValidationOnly() {
        return new Stories(filter(stories, new Predicate<Story>() {
            @Override
            public boolean apply(Story story) {
                return story.hasState(DELIVERED);
            }
        }));
    }

    public Stories scheduledOnly() {
        return new Stories(filter(stories, new Predicate<Story>() {
            @Override
            public boolean apply(Story story) {
                return story.hasState(STARTED) || story.hasState(DELIVERED) || story.hasState(ACCEPTED) || story.hasState(REJECTED);
            }
        }));
    }

    public Stories waitingForEstimationOnly() {
        return new Stories(filter(stories, new Predicate<Story>() {
            @Override
            public boolean apply(Story story) {
                return story.isNotEstimated();
            }
        }));
    }

    public int getEstimation() {
        int estimation = 0;
        for (Story story : stories) {
            if(story.isEstimated()) {
                estimation += story.getEstimation();
            }
        }
        return estimation;
    }
}
