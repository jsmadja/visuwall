package com.visuwall.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Walls {

    private static final ConcurrentMap<String, Wall> walls = new ConcurrentHashMap<String, Wall>(){{
        put("wall", new Wall());
    }};

    public static Wall get(String wallId) {
        return walls.get(wallId);
    }
}
