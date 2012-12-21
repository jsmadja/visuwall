/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.visuwall.domain.walls;

import org.fest.util.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.LocaleServiceProviderPool;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.remove;

public class Walls {

    private static final ConcurrentMap<String, Wall> walls = new ConcurrentHashMap<String, Wall>();

    private static final Logger LOG = LoggerFactory.getLogger(Walls.class);

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

    public static void delete(String name) {
        Wall wall = get(name);
        if(wall != null) {
            wall.deleteConfiguration();
            walls.remove(name);
            wall.stop();
            LOG.info("Wall "+name+" has been deleted");
        }
    }
}
