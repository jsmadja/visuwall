package com.visuwall.plugin.pivotaltracker;

import static com.visuwall.api.domain.BuildState.FAILURE;
import static com.visuwall.api.domain.BuildState.SUCCESS;
import static com.visuwall.api.domain.BuildState.UNSTABLE;
import com.visuwall.client.pivotaltracker.resource.Project;
import com.visuwall.api.domain.BuildState;

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
