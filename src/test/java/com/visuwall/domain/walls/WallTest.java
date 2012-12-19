package com.visuwall.domain.walls;

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.plugins.PluginDiscover;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WallTest {

    @Mock
    private PluginDiscover pluginDiscover;

    @Mock
    private VisuwallPlugin plugin;

    @Test
    public void should_save_wall_to_filesystem() {
        Wall wall = new Wall();
        wall.deleteConfiguration();

        ReflectionTestUtils.setField(wall, "pluginDiscover", pluginDiscover);

        Connection connection = new Connection();
        connection.setLogin("login");
        connection.setPassword("password");
        connection.setBuildFilter("buildFilter");
        connection.setIncludeBuildNames(Arrays.asList("X", "Y"));
        connection.setIncludeMetricNames(Arrays.asList("W", "Z"));
        connection.setName("Sonar");
        connection.setUrl("http://nemo.sonarsource.org");

        when(pluginDiscover.findPluginCompatibleWith(connection)).thenReturn(plugin);


        wall.addConnection(connection);
        wall.save();
        assertThat(new File(wall.getName() + ".xml").exists()).isTrue();
    }

    @Test
    public void should_load_wall_from_filesystem() throws JAXBException {
        Wall wall = new Wall("configured-wall");
        wall.loadExistingConfiguration();
        Connection connection = wall.getConnections().getConnection("Sonar");
        assertThat(connection.getLogin()).isEqualTo("login");
        assertThat(connection.getBuildFilter()).isEqualTo("buildFilter");
        assertThat(connection.getIncludeBuildNames()).isEqualTo(Arrays.asList("X", "Y"));
        assertThat(connection.getIncludeMetricNames()).isEqualTo(Arrays.asList("W", "Z"));
        assertThat(connection.getName()).isEqualTo("Sonar");
        assertThat(connection.getUrl()).isEqualTo("http://nemo.sonarsource.org");
    }

}
