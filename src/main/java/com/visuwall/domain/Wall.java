package com.visuwall.domain;

import com.visuwall.api.exception.ConnectionException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.plugin.demo.DemoConnection;
import com.visuwall.plugin.demo.DemoPlugin;
import com.visuwall.plugin.jenkins.JenkinsConnection;
import com.visuwall.plugin.jenkins.JenkinsPlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Wall {

    private Builds builds = new Builds();

    private Configuration configuration = new Configuration();

    private Plugins plugins = new Plugins();

    public Wall() {
        Collection<URL> urls = configuration.getUrls();
        for (URL url : urls) {
            addConnection(url);
        }
        start();
    }

    private void addConnection(URL url) {
        for (VisuwallPlugin plugin : plugins) {
            PluginConfiguration pluginConfiguration = PluginConfiguration.noConfiguration;
            if(plugin.accept(url, pluginConfiguration)) {
                try {
                    BasicCapability connection = plugin.getConnection(url, pluginConfiguration);
                    builds.addConnection(connection);
                } catch (ConnectionException e) {
                }
            }
        }
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        builds.refresh();
                        MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public Builds getBuilds() {
        return builds;
    }

}


