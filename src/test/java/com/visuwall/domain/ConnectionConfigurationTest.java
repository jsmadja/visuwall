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
        connectionConfiguration.setBuildFilter("A.*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("Azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter("a.*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a_or_A() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter("[aA].*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("azerty"));
        assertTrue(connectionConfiguration.acceptBuildNamedAs("Azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzerty"));
    }

    @Test
    public void should_accept_any_names_containing_with_ert() throws Exception {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        connectionConfiguration.setBuildFilter(".*ert.*");
        assertTrue(connectionConfiguration.acceptBuildNamedAs("azerty"));
        assertTrue(connectionConfiguration.acceptBuildNamedAs("Azerty"));
        assertFalse(connectionConfiguration.acceptBuildNamedAs("bzeprty"));
    }

}
