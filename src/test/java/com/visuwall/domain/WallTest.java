package com.visuwall.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WallTest {

    @Test
    public void test() throws InterruptedException {

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

}
