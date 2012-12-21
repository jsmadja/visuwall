package com.visuwall.domain.walls;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class WallConfiguratorTest {

    @Test
    public void should_save_wall_to_filesystem() {
        Wall wall = new Wall();
        wall.deleteConfiguration();

        WallConfigurator wallConfigurator = new WallConfigurator(wall);

        assertThat(wallConfigurator.configurationFile().exists()).isFalse();
        wallConfigurator.save();
        assertThat(wallConfigurator.configurationFile().exists()).isTrue();
    }
}
