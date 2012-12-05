package com.visuwall.domain;

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.plugin.bamboo.BambooPlugin;
import com.visuwall.plugin.continuum.ContinuumPlugin;
import com.visuwall.plugin.demo.DemoPlugin;
import com.visuwall.plugin.deployit.DeployItPlugin;
import com.visuwall.plugin.hudson.HudsonPlugin;
import com.visuwall.plugin.jenkins.JenkinsPlugin;
import com.visuwall.plugin.sonar.SonarPlugin;
import com.visuwall.plugin.teamcity.TeamCityPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Plugins implements Iterable<VisuwallPlugin> {

    private Collection<VisuwallPlugin> plugins = new ArrayList<VisuwallPlugin>();

    public Plugins() {
        plugins.add(new JenkinsPlugin());
        plugins.add(new DemoPlugin());
        plugins.add(new HudsonPlugin());
        plugins.add(new TeamCityPlugin());
        plugins.add(new BambooPlugin());
        plugins.add(new SonarPlugin());
        plugins.add(new DeployItPlugin());
        plugins.add(new ContinuumPlugin());
    }

    @Override
    public Iterator<VisuwallPlugin> iterator() {
        return plugins.iterator();
    }
}
