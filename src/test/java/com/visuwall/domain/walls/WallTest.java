package com.visuwall.domain.walls;

import com.visuwall.domain.builds.Build;
import com.visuwall.domain.builds.Builds;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.analyses.Analyses;
import com.visuwall.domain.analyses.Analysis;
import com.visuwall.domain.analyses.Metric;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WallTest {

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
            Builds builds = wall.getBuilds();
            for (Build build : builds) {
                String name = build.getName();
                System.err.println(name);
            }
            TimeUnit.SECONDS.sleep(5);
        }

    }

}
