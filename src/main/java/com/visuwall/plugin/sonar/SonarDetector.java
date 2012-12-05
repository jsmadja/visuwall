package com.visuwall.plugin.sonar;

import java.net.URL;

import com.visuwall.client.common.GenericSoftwareClient;

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
