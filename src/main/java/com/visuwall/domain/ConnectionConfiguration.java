package com.visuwall.domain;

import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionConfiguration {

    private String name;
    private String url;
    private String login;
    private String password;
    private String buildFilter;


    public static PluginConfiguration createPluginConfigurationFrom(ConnectionConfiguration connectionConfiguration) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.put("login", connectionConfiguration.getLogin());
        pluginConfiguration.put("password", connectionConfiguration.getPassword());
        return pluginConfiguration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public URL asUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean acceptBuildNamedAs(String name) {
        if(StringUtils.isBlank(buildFilter)) {
            return true;
        }
        return name.matches(buildFilter);
    }

    public void setBuildFilter(String buildFilter) {
        this.buildFilter = buildFilter;
    }
}
