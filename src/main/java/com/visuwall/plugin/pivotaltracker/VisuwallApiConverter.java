package com.visuwall.plugin.pivotaltracker;

import com.visuwall.client.pivotaltracker.resource.CurrentState;
import com.visuwall.client.pivotaltracker.resource.Stories;
import com.visuwall.client.pivotaltracker.resource.Story;
import com.visuwall.client.pivotaltracker.resource.StoryType;

import static com.visuwall.api.domain.Story.State.*;
import static com.visuwall.api.domain.Story.State.UNKNOWN;
import static com.visuwall.client.pivotaltracker.resource.CurrentState.*;

public class VisuwallApiConverter {

    public static com.visuwall.api.domain.Stories asVisuwallStories(Stories stories) {
        com.visuwall.api.domain.Stories _stories = new com.visuwall.api.domain.Stories();
        for (Story story : stories) {
            if(story.getStoryType() != StoryType.release) {
                com.visuwall.api.domain.Story _story = asVisuwallStory(story);
                _stories.add(_story);
            }
        }
        return _stories;
    }

    private static com.visuwall.api.domain.Story asVisuwallStory(Story story) {
        com.visuwall.api.domain.Story _story = new com.visuwall.api.domain.Story();
        _story.setEstimation(story.getEstimate());
        _story.setState(asVisuwallState(story.getCurrentState()));
        return _story;
    }

    private static com.visuwall.api.domain.Story.State asVisuwallState(CurrentState currentState) {
        if(currentState == accepted) {
            return ACCEPTED;
        }
        if(currentState == unstarted) {
            return UNSTARTED;
        }
        if(currentState == delivered) {
            return DELIVERED;
        }
        if(currentState == started) {
            return STARTED;
        }
        return UNKNOWN;
    }
}
