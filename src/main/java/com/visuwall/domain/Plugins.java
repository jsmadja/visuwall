package com.visuwall.domain;

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.plugin.demo.DemoPlugin;
import com.visuwall.plugin.jenkins.JenkinsPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Plugins implements Iterable<VisuwallPlugin> {

    private Collection<VisuwallPlugin> plugins = new ArrayList<VisuwallPlugin>();

    public Plugins() {
        plugins.add(new JenkinsPlugin());
        plugins.add(new DemoPlugin());
    }

    @Override
    public Iterator<VisuwallPlugin> iterator() {
        return plugins.iterator();
    }
}
