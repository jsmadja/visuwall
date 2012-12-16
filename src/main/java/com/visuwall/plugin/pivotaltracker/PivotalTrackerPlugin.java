package com.visuwall.plugin.pivotaltracker;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.client.common.GenericSoftwareClient;
import com.visuwall.client.common.ResourceNotFoundException;
import com.visuwall.domain.plugins.PluginConfiguration;

import java.net.URL;

public class PivotalTrackerPlugin implements VisuwallPlugin<PivotalTrackerConnection> {

    @Override
    public PivotalTrackerConnection getConnection(URL url, PluginConfiguration pluginConfiguration) {
        String login = pluginConfiguration.get("login");
        String password = pluginConfiguration.get("password");
        PivotalTrackerConnection pivotalTrackerConnection = new PivotalTrackerConnection();
        pivotalTrackerConnection.connect(url.toString(), login, password);
        return pivotalTrackerConnection;
    }

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<PivotalTrackerConnection> getConnectionClass() {
        return PivotalTrackerConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "PivotalTracker plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        GenericSoftwareClient genericSoftwareClient = new GenericSoftwareClient();
        try {
            String download = genericSoftwareClient.download(url);
            if(!download.contains("Pivotal Tracker")) {
                throw new SoftwareNotFoundException("Url " + url + " is not compatible with Pivotal Tracker");
            }
        } catch (ResourceNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Pivotal Tracker", e);
        }
        SoftwareId softwareId = new SoftwareId();
        softwareId.setCompatible(true);
        softwareId.setName("PivotalTracker");
        return softwareId;
    }

    @Override
    public boolean accept(URL url, PluginConfiguration pluginConfiguration) {
        try {
            getSoftwareId(url, pluginConfiguration);
        } catch (SoftwareNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean requiresPassword() {
        return true;
    }

}
