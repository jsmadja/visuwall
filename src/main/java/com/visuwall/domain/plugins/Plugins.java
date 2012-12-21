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

package com.visuwall.domain.plugins;

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.plugin.bamboo.BambooPlugin;
import com.visuwall.plugin.continuum.ContinuumPlugin;
import com.visuwall.plugin.demo.DemoPlugin;
import com.visuwall.plugin.deployit.DeployItPlugin;
import com.visuwall.plugin.hudson.HudsonPlugin;
import com.visuwall.plugin.jenkins.JenkinsPlugin;
import com.visuwall.plugin.pivotaltracker.PivotalTrackerPlugin;
import com.visuwall.plugin.sonar.SonarPlugin;
import com.visuwall.plugin.teamcity.TeamCityPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public class Plugins implements Iterable<VisuwallPlugin> {

    private static final Collection<VisuwallPlugin> plugins = new ConcurrentLinkedQueue<VisuwallPlugin>();

    public Plugins() {
        plugins.add(new JenkinsPlugin());
        plugins.add(new SonarPlugin());
        plugins.add(new HudsonPlugin());
        plugins.add(new PivotalTrackerPlugin());
        plugins.add(new TeamCityPlugin());
        plugins.add(new BambooPlugin());
        plugins.add(new ContinuumPlugin());
        plugins.add(new DeployItPlugin());
        plugins.add(new DemoPlugin());
    }

    @Override
    public Iterator<VisuwallPlugin> iterator() {
        return plugins.iterator();
    }
}
