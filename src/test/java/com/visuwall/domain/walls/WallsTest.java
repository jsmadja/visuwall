package com.visuwall.domain.walls;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class WallsTest {

    @Test
    public void should_recognize_configuration_filename() {

        String filename = "wall-default.txml";

        Assertions.assertThat(filename.matches("wall-[a-zA-Z\\.]+xml")).isTrue();

    }

}
