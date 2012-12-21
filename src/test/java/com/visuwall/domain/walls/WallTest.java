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

import com.visuwall.api.plugin.VisuwallPlugin;
import com.visuwall.domain.connections.Connection;
import com.visuwall.domain.plugins.PluginDiscover;
import org.junit.Ignore;
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

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class WallTest {

    @Mock
    private PluginDiscover pluginDiscover;

    @Mock
    private VisuwallPlugin plugin;

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
