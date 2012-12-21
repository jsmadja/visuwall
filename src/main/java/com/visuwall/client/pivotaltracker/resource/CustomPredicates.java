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

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.visuwall.client.pivotaltracker.resource.CurrentState.unstarted;
import static com.visuwall.client.pivotaltracker.resource.StoryType.feature;

public class CustomPredicates {

    private static final Logger LOG = LoggerFactory.getLogger(CustomPredicates.class);

    public static Predicate<Story> isFeature = new Predicate<Story>() {
        @Override
        public boolean apply(Story story) {
            StoryType storyType = story.getStoryType();
            if (storyType == null) {
                LOG.warn("Story " + story.getId() + " has no story type");
                return false;
            }
            return storyType.equals(feature);
        }
    };

    public static Predicate<Story> isEstimated = new Predicate<Story>() {
        @Override
        public boolean apply(Story story) {
            return story.getEstimate() != null;
        }
    };

    public static Predicate<Story> isReady = new Predicate<Story>() {
        @Override
        public boolean apply(Story story) {
            CurrentState currentState = story.getCurrentState();
            if (currentState == null) {
                LOG.warn("Story " + story.getId() + " has no current state");
                return false;
            }
            return currentState.equals(unstarted);
        }
    };

}
