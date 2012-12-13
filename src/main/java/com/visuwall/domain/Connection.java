package com.visuwall.domain;

import com.google.common.base.Joiner;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.plugin.capability.BasicCapability;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

public class Connection {

    @JsonIgnore
    private BasicCapability basicCapability;

    private String name;

    private String url;

    private String login;

    @JsonIgnore
    private String password;

    private String buildFilter;

    public Collection<SoftwareProjectId> listSoftwareProjectIds() {
        return basicCapability.listSoftwareProjectIds().keySet();
    }

    @JsonIgnore
    public BasicCapability getVisuwallConnection() {
        return basicCapability;
    }

    public static PluginConfiguration createPluginConfigurationFrom(Connection connection) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.put("login", connection.getLogin());
        pluginConfiguration.put("password", connection.getPassword());
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

    public boolean accept(String name) {
        if(isBlank(buildFilter)) {
            return true;
        }
        return name.matches(buildFilter);
    }

    public void setBuildFilter(String buildFilter) {
        if(buildFilter.startsWith("regexp:")) {
            this.buildFilter = buildFilter.substring("regexp:".length());
            return;
        }
        if(buildFilter.contains(",")) {
            List<String> regexps = buildRegexps(buildFilter);
            this.buildFilter = Joiner.on('|').join(regexps);
        } else {
            this.buildFilter = buildFilter;
        }
    }

    private List<String> buildRegexps(String buildFilter) {
        List<String> regexps = new ArrayList<String>();
        String[] split = buildFilter.split(",");
        for (String filter : split) {
            String regexp = "(?i)";
            if(filter.endsWith("*")) {
                String buildNamePrefix = filter.substring(0, filter.length() - 1);
                regexp +=  buildNamePrefix + ".*";
            } else {
                regexp += filter.substring(0, filter.length());
            }
            regexps.add(regexp);
        }
        return regexps;
    }

    public void setVisuwallConnection(BasicCapability visuwallConnection) {
        this.basicCapability = visuwallConnection;
    }

    @Override
    public String toString() {
        return "connection '"+name+"";
    }

    public boolean hasName(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    public String getBuildFilter() {
        return buildFilter;
    }
}
