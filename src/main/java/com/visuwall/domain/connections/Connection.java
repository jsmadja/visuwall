package com.visuwall.domain.connections;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.quality.QualityMetric;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.MetricCapability;
import com.visuwall.domain.analyses.Metric;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.util.Collections.sort;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.removeEnd;

public class Connection {

    @JsonIgnore
    private BasicCapability basicCapability;

    private String name;

    private String url;

    private String login;

    @JsonIgnore
    private String password;

    private String buildFilter;
    private String pluginName;
    private String warning;

    private List<String> includeBuildNames = new ArrayList<String>();
    private List<String> includeMetricNames = new ArrayList<String>();

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
        if(url != null) {
            url = url.toLowerCase();
            url = removeEnd(url, "/");
        }
        this.url = url;
    }

    public List<String> getIncludeBuildNames() {
        return includeBuildNames;
    }

    public void setIncludeBuildNames(List<String> includeBuildNames) {
        this.includeBuildNames = includeBuildNames;
    }

    public Collection<String> getBuildNames() {
        List<String> buildNames = new ArrayList<String>();
        if (basicCapability != null) {
            Collection<SoftwareProjectId> softwareProjectIds = listSoftwareProjectIds();
            for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
                buildNames.add(softwareProjectId.getProjectId());
            }
            sort(buildNames);
        }
        return buildNames;
    }

    public List<String> getIncludeMetricNames() {
        return includeMetricNames;
    }

    public void setIncludeMetricNames(List<String> includeMetricNames) {
        this.includeMetricNames = includeMetricNames;
    }

    public Collection<Metric> getMetrics() {
        Set<Metric> metrics = new TreeSet<Metric>();
        if(basicCapability instanceof MetricCapability) {
            MetricCapability capability = (MetricCapability) basicCapability;
            Map<String, List<QualityMetric>> metricsByCategory = capability.getMetricsByCategory();
            Collection<List<QualityMetric>> values = metricsByCategory.values();
            for (List<QualityMetric> value : values) {
                for (QualityMetric qualityMetric : value) {
                    String key = qualityMetric.getKey();
                    String name = qualityMetric.getName();
                    metrics.add(new Metric(key, name));
                }
            }
        }
        return metrics;
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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public boolean accept(String name) {
        if (!includeBuildNames.isEmpty()) {
            return includeBuildNames.contains(name);
        }
        if (isBlank(buildFilter)) {
            return true;
        }
        String regexp = new RegexpAdapter(buildFilter).toString();
        return name.matches(regexp);
    }

    public void setBuildFilter(String buildFilter) {
        this.buildFilter = buildFilter;
    }

    public void setVisuwallConnection(BasicCapability visuwallConnection) {
        this.basicCapability = visuwallConnection;
    }

    @Override
    public String toString() {
        return "connection '" + name + "";
    }

    public boolean hasName(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    public String getBuildFilter() {
        return buildFilter;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getWarning() {
        return warning;
    }
}
