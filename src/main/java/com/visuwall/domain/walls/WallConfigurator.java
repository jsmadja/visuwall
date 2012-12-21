package com.visuwall.domain.walls;

import com.google.common.io.Closeables;
import org.fest.util.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class WallConfigurator {

    private final Wall wall;

    private static final Logger LOG = LoggerFactory.getLogger(WallConfigurator.class);

    WallConfigurator(Wall wall) {
        this.wall = wall;
    }

    public File configurationFile() {
        return new File("wall-"+wall.getName()+".xml");
    }

    public void save() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(configurationFile());
            JAXBContext jaxbContext = JAXBContext.newInstance(Wall.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(this, fileOutputStream);
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
        Files.delete(configurationFile());
    }

    public Wall loadWall() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Wall.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Wall) unmarshaller.unmarshal(configurationFile());
    }
}

