package com.visuwall.domain.walls;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Walls {

    private static final ConcurrentMap<String, Wall> walls = new ConcurrentHashMap<String, Wall>();

    static {
        Wall wall = new Wall("wall");
        walls.put("wall", wall);
        wall.start();
    }

    public static Wall get(String wallId) {
        return walls.get(wallId);
    }

    public static Collection<Wall> all() {
        return walls.values();
    }
}
