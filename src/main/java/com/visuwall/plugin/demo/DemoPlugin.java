package com.visuwall.plugin.demo;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.exception.SoftwareNotFoundException;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.domain.plugins.PluginConfiguration;

import java.net.URL;

public abstract class DemoPlugin <T extends BasicCapability> implements VisuwallPlugin<T>  {

    @Override
    public T getConnection(URL url, PluginConfiguration pluginConfiguration) {
        T connection = getConnection();
        connection.connect(url.toString(), null, null);
        return connection;
    }

    protected abstract T getConnection();

    @Override
    public PluginConfiguration getDefaultPluginConfiguration() {
        return PluginConfiguration.noConfiguration;
    }

    @Override
    public Class<T> getConnectionClass() {
        return (Class<T>) getConnection().getClass();
    }

    @Override
    public SoftwareId getSoftwareId(URL url, PluginConfiguration pluginConfiguration) throws SoftwareNotFoundException {
        if (url == null || !getExpectedUrl().equals(url.toString())) {
            throw new SoftwareNotFoundException(getName() + " is not compatible with url : " + url);
        }
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName(getName());
        softwareId.setCompatible(true);
        softwareId.setVersion("1.0");
        softwareId.setWarnings("This is a demo plugin");
        return softwareId;
    }

    @Override
    public boolean accept(URL url, PluginConfiguration pluginConfiguration) {
        return url != null && getExpectedUrl().equals(url.toString());
    }

    protected abstract String getExpectedUrl();

    @Override
    public boolean requiresPassword() {
        return false;
    }

}
