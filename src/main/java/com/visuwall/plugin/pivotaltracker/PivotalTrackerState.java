package com.visuwall.plugin.pivotaltracker;

import com.visuwall.api.domain.BuildState;
import com.visuwall.client.pivotaltracker.resource.Project;

import static com.visuwall.api.domain.BuildState.*;

public class PivotalTrackerState {

    public BuildState guessState(Project project) {
        float nbSprints = project.getNbSprints();
        if (nbSprints > 2) {
            return SUCCESS;
        }
        if (nbSprints >= 1) {
            return UNSTABLE;
        }
        return FAILURE;
    }
}
