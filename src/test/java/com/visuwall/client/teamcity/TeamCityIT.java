package com.visuwall.client.teamcity;

import com.visuwall.client.teamcity.exception.TeamCityProjectsNotFoundException;
import com.visuwall.client.teamcity.resource.TeamCityProject;
import org.fest.assertions.Assertions;
import org.junit.Test;

import java.util.List;

public class TeamCityIT {

    @Test
    public void should_list_all_projects() throws TeamCityProjectsNotFoundException {
        TeamCity teamCity = new TeamCity("http://teamcity-master", "guest", "");
        List<TeamCityProject> projects = teamCity.findAllProjects();
        for (TeamCityProject project : projects) {
            System.err.println(project.getName());
        }
        Assertions.assertThat(projects).hasSize(9);
    }

}
