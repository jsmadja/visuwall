package com.visuwall.plugin.pivotaltracker;

import com.visuwall.api.domain.BuildTime;
import com.visuwall.client.pivotaltracker.resource.Iteration;

import java.util.Date;

public class BuildTimer {

    private Iteration iteration;

    public BuildTimer(Iteration iteration) {
        this.iteration = iteration;
    }

    public BuildTime build() {
        BuildTime buildTime = new BuildTime();
        buildTime.setDuration(computeRemainingTime());
        return buildTime;
    }

    private long computeRemainingTime() {
        long now = new Date().getTime();
        Date finish = iteration.getFinish();
        long finishTime = finish.getTime();
        return finishTime - now;
    }

}
