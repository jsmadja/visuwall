package com.visuwall.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Configuration {

    private List<ConnectionConfiguration> connectionConfigurations = new ArrayList<ConnectionConfiguration>();

    public Collection<ConnectionConfiguration> getConnectionConfigurations() {
        return unmodifiableList(connectionConfigurations);
    }

    public void addUrl(ConnectionConfiguration connectionConfiguration) {
        this.connectionConfigurations.add(connectionConfiguration);
    }
}
