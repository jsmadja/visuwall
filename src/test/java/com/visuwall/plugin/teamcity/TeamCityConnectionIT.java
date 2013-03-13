package com.visuwall.plugin.teamcity;

import com.visuwall.api.domain.SoftwareProjectId;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class TeamCityConnectionIT {

    @Test
    public void should_list_all_projects() {

        TeamCityConnection teamCityConnection = new TeamCityConnection();
        teamCityConnection.connect("http://teamcity-master", "guest", "");
        Map<SoftwareProjectId,String> projects = teamCityConnection.listSoftwareProjectIds();
        Set<SoftwareProjectId> softwareProjectIds = projects.keySet();
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            System.err.println(softwareProjectId);
        }

    }

}
