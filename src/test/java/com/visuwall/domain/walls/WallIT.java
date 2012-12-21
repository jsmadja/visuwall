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

package com.visuwall.domain.walls;

import com.visuwall.domain.analyses.Analyses;
import com.visuwall.domain.analyses.Analysis;
import com.visuwall.domain.analyses.Metric;
import com.visuwall.domain.builds.Build;
import com.visuwall.domain.builds.Builds;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.tracks.Track;
import com.visuwall.domain.tracks.Tracks;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WallIT {

    @Ignore
    @Test
    public void test_sonar() throws InterruptedException {

        Wall wall = Walls.get("wall");
        Connection connection = new Connection();
        connection.setUrl("http://nemo.sonarsource.org");
        connection.setName("sonar");
        connection.setIncludeBuildNames(Arrays.asList("Devpad"));
        wall.addConnection(connection);

        while(true) {
            System.err.println("Tick");
            Analyses analyses = wall.getAnalyses();
            for (Analysis analyse : analyses) {
                System.err.println(analyse);
                Set<Metric> metrics = analyse.getMetrics();
                for (Metric metric : metrics) {
                    System.err.println(metric);
                }
            }
            TimeUnit.SECONDS.sleep(5);
        }

    }

    @Ignore
    @Test
    public void test_pivotal_tracker() throws InterruptedException {
        Wall wall = Walls.get("wall");
        Connection connection = new Connection();
        connection.setUrl("https://www.pivotaltracker.com");
        connection.setName("pivotal");
        connection.setLogin("jsmadja@financeactive.com");
        connection.setPassword("xedy4bsa");
        wall.addConnection(connection);
        while(true) {
            System.err.println("Tick");
            Tracks tracks = wall.getTracks();
            for (Track track : tracks) {
                System.err.println(track.getName());
                System.err.println("days to go:"+track.getDaysToGo());
                System.err.println("scheduled:"+track.getScheduledStories());
                System.err.println("accepted:"+track.getAcceptedStories());
                System.err.println("in progress:"+track.getStoriesInProgress());
                System.err.println("remaining:"+track.getRemainingStories());
                System.err.println("in validation:"+track.getStoriesInValidation());

                System.err.println("---");
                System.err.println("available:"+track.getAvailableStories());
                System.err.println("velocity:"+track.getActualVelocity());
                System.err.println("# sprints:"+track.getNumberOfSprints());
                System.err.println("to estimate:"+track.getWaitingForEstimationStories());
            }
            TimeUnit.SECONDS.sleep(5);
        }

    }

    @Test
    public void test_cloudbees() throws Exception {
        Wall wall = Walls.get("wall");
        Connection connection = new Connection();
        connection.setUrl("https://jsmadja.ci.cloudbees.com");
        connection.setName("cloudbees");
        wall.addConnection(connection);
        while(true) {
            System.err.println("Tick");
            Builds builds = wall.getBuilds();
            for (Build build : builds) {
                System.err.println(build);
            }
            TimeUnit.SECONDS.sleep(5);
        }

    }

}
