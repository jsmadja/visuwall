package com.visuwall.plugin.sonar;

import com.visuwall.client.common.GenericSoftwareClient;

import java.net.URL;

class SonarDetector {

    private GenericSoftwareClient client = new GenericSoftwareClient();

    public boolean isSonarPropertiesPage(URL url) {
        return client.exist(buildPropertiesUrl(url), Properties.class);
    }

    public boolean isSonarWelcomePage(URL url) {
        return client.contains(url, "Sonar");
    }

    public String buildPropertiesUrl(URL url) {
        return url.toString() + "/api/properties/sonar.core.version";
    }

}
