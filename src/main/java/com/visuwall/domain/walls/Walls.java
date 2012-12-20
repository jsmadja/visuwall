package com.visuwall.domain.walls;

import org.apache.commons.lang.StringUtils;
import org.fest.util.Files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.remove;

public class Walls {

    private static final ConcurrentMap<String, Wall> walls = new ConcurrentHashMap<String, Wall>();

    static {
        createWallsFromConfigurationFiles();
    }

    private static void createWallsFromConfigurationFiles() {
        String[] configurationFiles = findConfigurationFiles();
        createWallsFrom(configurationFiles);
    }

    private static void createWallsFrom(String[] configurationFiles) {
        for (String configurationFile : configurationFiles) {
            String wallName = remove(configurationFile.split(".xml")[0], "wall-");
            createWall(wallName);
        }
    }

    private static Wall createWall(String wallName) {
        Wall wall = new Wall(wallName);
        wall.start();
        walls.put(wallName, wall);
        return wall;
    }

    private static String[] findConfigurationFiles() {
        return Files.currentFolder().list(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.matches("wall-[a-zA-Z\\\\.]+xml");
                }
            });
    }

    public static Wall get(String wallId) {
        if(isBlank(wallId)) {
            wallId = "default";
        }
        if(walls.containsKey(wallId)) {
            return walls.get(wallId);
        }
        return createWall(wallId);
    }

    public static Collection<Wall> all() {
        return walls.values();
    }
}
