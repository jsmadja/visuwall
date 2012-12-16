package com.visuwall.api.plugin.capability;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;
import org.joda.time.DateMidnight;

public interface TrackCapability {

    int getRemainingPointsInCurrentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException;

    int getVelocity(SoftwareProjectId projectId) throws ProjectNotFoundException;

    int getEstimatedPointsInFuture(SoftwareProjectId projectId) throws ProjectNotFoundException;

    DateMidnight getEndOfCurrentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException;
}
