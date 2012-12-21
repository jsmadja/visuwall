package com.visuwall.plugin.bamboo;


import com.visuwall.api.domain.SoftwareId;
import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.domain.plugins.PluginConfiguration;
import org.junit.Test;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class BambooPluginIT {

    @Test
    public void test_bamboo() throws Exception {
        PluginConfiguration configuration = new PluginConfiguration();
        URL url = new URL("https://www.avato.net/demo/bamboo");
        BambooPlugin bambooPlugin = new BambooPlugin();
        SoftwareId softwareId = bambooPlugin.getSoftwareId(url, configuration);
        System.err.println(softwareId);
        BambooConnection connection = bambooPlugin.getConnection(url, configuration);
        Map<SoftwareProjectId,String> softwareProjectIdStringMap = connection.listSoftwareProjectIds();
        Collection<String> values = softwareProjectIdStringMap.values();
        for (String value : values) {
            System.err.println(value);
        }
    }

}