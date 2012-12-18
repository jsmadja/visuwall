package com.visuwall.api.plugin.capability;

import com.visuwall.api.domain.Backlog;
import com.visuwall.api.domain.Iteration;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.exception.ProjectNotFoundException;

public interface TrackCapability {

    int getVelocity(SoftwareProjectId projectId) throws ProjectNotFoundException;

    Iteration getCurrentIteration(SoftwareProjectId projectId) throws ProjectNotFoundException;

    Backlog getBackLog(SoftwareProjectId projectId) throws ProjectNotFoundException;
}
