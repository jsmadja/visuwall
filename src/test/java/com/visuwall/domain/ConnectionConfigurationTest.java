package com.visuwall.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectionConfigurationTest {

    @Test
    public void should_accept_any_names_when_there_is_no_build_filter() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        assertTrue(connectionConfiguration.acceptBuildNamedAs("mybuild"));
    }

    @Test
    public void should_accept_any_names_starting_with_A() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter("regexp:A.*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("Azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter("regexp:a.*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a_or_A() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter("regexp:(?i)a.*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("azerty"));
        assertTrue(connectionConfiguration.acceptBuildNamedAs("Azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzerty"));
    }

    @Test
    public void should_accept_any_names_containing_with_ert() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter("regexp:.*ert.*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("azerty"));
        assertTrue(connectionConfiguration.acceptBuildNamedAs("Azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzeprty"));
    }

    @Test
    public void should_accept_any_names_containing_with_names_split_by_comma() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter("cautions*,fxent,Hermes");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("cautions"));
        assertTrue(connectionConfiguration.acceptBuildNamedAs("fxent"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("fxent-it"));
        assertTrue(connectionConfiguration.acceptBuildNamedAs("cautions-it"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzeprty"));
        assertTrue(connectionConfiguration.acceptBuildNamedAs("hermes"));
    }

}
