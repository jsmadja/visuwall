package com.visuwall.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

public class Configuration {

    public Collection<URL> getUrls() {
        try {
            //return Arrays.asList(new URL("http://ci.awired.net/jenkins/"));
            return Arrays.asList(new URL("http://jenkins-master"));

            //return Arrays.asList(new URL("http://demo.visuwall.ci"));
        } catch (MalformedURLException e) {
            return Arrays.asList();
        }
    }
}
