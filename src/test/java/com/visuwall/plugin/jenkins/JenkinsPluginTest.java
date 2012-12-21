package com.visuwall.plugin.jenkins;

import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class JenkinsPluginTest {

    @Test
    public void test_cloudbees() throws Exception {
        PluginConfiguration configuration = new PluginConfiguration();
        configuration.put("login", "jsmadja@xebia.fr");
        configuration.put("password", "xedy4bsa");
        URL url = new URL("https://jsmadja.ci.cloudbees.com");
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        SoftwareId softwareId = jenkinsPlugin.getSoftwareId(url, configuration);
        System.err.println(softwareId);
        JenkinsConnection connection = jenkinsPlugin.getConnection(url, configuration);
        Map<SoftwareProjectId,String> softwareProjectIdStringMap = connection.listSoftwareProjectIds();
        Collection<String> values = softwareProjectIdStringMap.values();
        for (String value : values) {
            System.err.println(value);
        }
    }

}
