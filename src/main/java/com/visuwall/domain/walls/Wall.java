package com.visuwall.domain.walls;

import com.google.common.io.Closeables;
import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.api.plugin.capability.BasicCapability;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.MetricCapability;
import com.visuwall.api.plugin.capability.TrackCapability;
import com.visuwall.domain.RefreshableNotFoundException;
import com.visuwall.domain.analyses.Analyses;
import com.visuwall.domain.analyses.Analysis;
import com.visuwall.domain.builds.Build;
import com.visuwall.domain.builds.Builds;
import com.visuwall.domain.connections.Configuration;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.connections.Connections;
import com.visuwall.domain.plugins.PluginConfiguration;
import com.visuwall.domain.plugins.PluginDiscover;
import com.visuwall.domain.tracks.Track;
import com.visuwall.domain.tracks.Tracks;
import com.visuwall.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.fest.util.Files.delete;

@XmlRootElement(name = "wall")
@XmlAccessorType(XmlAccessType.FIELD)
public class Wall implements Runnable, Comparable<Wall> {

    private String name;

    @XmlTransient
    private Builds builds = new Builds();

    @XmlTransient
    private Analyses analyses = new Analyses();

    @XmlTransient
    private Tracks tracks = new Tracks();

    private Configuration configuration = new Configuration();

    private static final Logger LOG = LoggerFactory.getLogger(Wall.class);

    @XmlTransient
    private PluginDiscover pluginDiscover = new PluginDiscover();

    @XmlTransient
    private Thread thread;

    Wall() {}

    // TODO: WallConfiguration

    public Wall(String name) {
        this.name = name;
    }

    public void addConnection(Connection connection) {
        String url = connection.getUrl();
        LOG.info("["+name+"] Trying to identify a compatible plugin for url:" + url);
        VisuwallPlugin plugin = pluginDiscover.findPluginCompatibleWith(connection);
        if (plugin == null) {
            LOG.info("["+name+"] Visuwall cannot find a compatible plugin for " + url);
        } else {
            LOG.info(plugin.getName() + " is compatible with url:" + url);
            addNewValidConnection(connection, plugin);
            LOG.info("["+name+"] New connection established for " + url);
        }
    }

    private void addNewValidConnection(Connection connection, VisuwallPlugin plugin) {
        PluginConfiguration pluginConfiguration = Connection.createPluginConfigurationFrom(connection);
        URL softwareUrl = connection.asUrl();
        BasicCapability visuwallConnection = plugin.getConnection(softwareUrl, pluginConfiguration);
        connection.setVisuwallConnection(visuwallConnection);
        if (visuwallConnection instanceof MetricCapability) {
            analyses.addConnection(connection);
        }
        if (visuwallConnection instanceof BuildCapability) {
            builds.addConnection(connection);
        }
        if (visuwallConnection instanceof TrackCapability) {
            tracks.addConnection(connection);
        }
        configuration.addUrl(connection);
    }

    void start() {
        thread = new Thread(this);
        thread.start();
    }

    public Builds getBuilds() {
        return builds;
    }

    public Analyses getAnalyses() {
        return analyses;
    }

    public Tracks getTracks() {
        return tracks;
    }

    public Build getBuild(String name) throws RefreshableNotFoundException {
        return builds.get(name);
    }

    public Analysis getAnalysis(String name) throws RefreshableNotFoundException {
        return analyses.get(name);
    }

    public Track getTrack(String name) throws RefreshableNotFoundException {
        return tracks.get(name);
    }

    @Override
    public void run() {
        if(configurationFileExists()) {
            try {
                loadExistingConfiguration();
            } catch (JAXBException e) {
                LOG.warn("["+name+"] Unable to load existing configuration from ("+configurationFile().getAbsolutePath()+")", e);
            }
        }
        while (true) {
            try {
                long start = System.currentTimeMillis();
                LOG.info("["+name+"] Wall is full refreshing ...");
                ExecutorService pool = Executors.newFixedThreadPool(20);
                builds.refresh(pool);
                analyses.refresh(pool);
                tracks.refresh(pool);
                pool.shutdown();
                LOG.info("["+name+"] Wall has been fully refreshed in " + duration(start) + " ms");
                waitForNextIteration();
            } catch (InterruptedException e) {
                LOG.error("["+name+"] Error in main loop", e);
            }
        }
    }

    private boolean configurationFileExists() {
        return configurationFile().exists();
    }

    public void loadExistingConfiguration() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Wall.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Wall configuredWall = (Wall) unmarshaller.unmarshal(configurationFile());
        for (Connection connection : configuredWall.getConnections()) {
            addConfiguredConnection(connection);
        }
        LOG.info("["+name+"] Wall has been successfully configured from file "+configurationFile().getAbsolutePath());
    }

    private void addConfiguredConnection(Connection connection) {
        VisuwallPlugin visuwallPlugin = pluginDiscover.findPluginCompatibleWith(connection);
        if(visuwallPlugin == null) {
            LOG.info("["+name+"] Cannot find compatible plugin with connection to "+connection.getUrl());
        } else {
            if(!visuwallPlugin.requiresPassword()) {
                addNewValidConnection(connection, visuwallPlugin);
            }
        }
    }

    private File configurationFile() {
        return new File("wall-"+name+".xml");
    }

    public void save() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(configurationFile());
            JAXBContext jaxbContext = JAXBContext.newInstance(Wall.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(this, fileOutputStream);
        } catch (FileNotFoundException e) {
            LOG.warn("["+name+"] Unable to save configuration to filesystem", e);
        } catch (JAXBException e) {
            LOG.warn("["+name+"] Unable to save wall '" + name + "' configuration to filesystem", e);
        } finally {
            Closeables.closeQuietly(fileOutputStream);
        }
    }

    private long duration(long start) {
        return System.currentTimeMillis() - start;
    }

    private void waitForNextIteration() throws InterruptedException {
        if (builds.count() == 0 && analyses.count() == 0 && tracks.count() == 0) {
            LOG.info("["+name+"] No refreshables, next refresh in 30 seconds");
            TimeUnit.SECONDS.sleep(20);
        } else {
            LOG.info("["+name+"] Next refresh in 1 minute");
            TimeUnit.MINUTES.sleep(1);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void removeConnection(String name) throws ResourceNotFoundException {
        if (!configuration.containsConfiguration(name)) {
            throw new ResourceNotFoundException("["+name+"] Cannot find connection '" + name + "'");
        }
        Connection connection = configuration.getConnectionByName(name);
        removeConnection(connection);
    }

    public void updateConnection(Connection connection) {
        removeConnection(connection);
        addConnection(connection);
        save();
    }

    private void removeConnection(Connection connection) {
        configuration.remove(connection);
        builds.removeAllFrom(connection);
        analyses.removeAllFrom(connection);
        tracks.removeAllFrom(connection);
        save();
    }

    public Connections getConnections() {
        return configuration.getConnections();
    }

    public String getName() {
        return name;
    }

    public void deleteConfiguration() {
        delete(configurationFile());
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public int compareTo(Wall wall) {
        return name.compareToIgnoreCase(wall.name);
    }
}


