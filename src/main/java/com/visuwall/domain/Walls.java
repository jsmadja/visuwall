package com.visuwall.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Walls {

    private static final ConcurrentMap<String, Wall> walls = new ConcurrentHashMap<String, Wall>();

    static {
        Wall wall = new Wall();
        walls.put("wall", wall);
        wall.start();
    }

    public static Wall get(String wallId) {
        return walls.get(wallId);
    }
}
