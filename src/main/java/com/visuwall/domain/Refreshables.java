package com.visuwall.domain;

import com.visuwall.domain.connections.Connection;

import java.util.Set;
import java.util.concurrent.ExecutorService;

public interface Refreshables<T extends Refreshable> {

    void addConnection(Connection connection);

    void removeAllFrom(Connection connection);

    int count();

    boolean contains(String name);

    Set<T> all();

    void refresh(ExecutorService pool);

}
