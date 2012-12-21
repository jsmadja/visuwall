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

import com.google.common.io.Closeables;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallConfigurator {

    private final Wall wall;

    private static final Logger LOG = LoggerFactory.getLogger(WallConfigurator.class);

    WallConfigurator(Wall wall) {
        this.wall = wall;
    }

    public File configurationFile() {
        try {
            File configurationFile = new File("wall-cfg/wall-" + wall.getName() + ".xml");
            Files.createParentDirs(configurationFile);
            return configurationFile;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create configuration directory (wall-cfg)", e);
        }
    }

    public void save() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(configurationFile());
            JAXBContext jaxbContext = JAXBContext.newInstance(Wall.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(wall, fileOutputStream);
        } catch (FileNotFoundException e) {
            LOG.warn("["+wall.getName()+"] Unable to save configuration to filesystem", e);
        } catch (JAXBException e) {
            LOG.warn("["+wall.getName()+"] Unable to save wall configuration to filesystem", e);
        } finally {
            Closeables.closeQuietly(fileOutputStream);
        }
    }

    public boolean configurationFileExists() {
        return configurationFile().exists();
    }

    public void delete() {
        configurationFile().delete();
    }

    public Wall loadWall() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Wall.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Wall) unmarshaller.unmarshal(configurationFile());
    }
}

