package com.visuwall.domain;

import com.visuwall.domain.connections.Connection;

import java.util.Set;

public interface Refreshables<T extends Refreshable> {

    void refreshAll();

    void addConnection(Connection connection);

    void removeAllFrom(Connection connection);

    int count();

    boolean contains(String name);

    Set<T> all();
}
