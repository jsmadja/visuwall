package com.visuwall.domain.connections;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectionTest {

    @Test
    public void should_accept_any_names_when_there_is_no_build_filter() throws Exception {
        Connection connectionConfiguration = new Connection();
        Assert.assertTrue(connectionConfiguration.accept("mybuild"));
    }

    @Test
    public void should_accept_any_names_starting_with_A() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:A.*");
        Assert.assertTrue(connectionConfiguration.accept("Azerty"));
        Assert.assertFalse(connectionConfiguration.accept("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:a.*");
        Assert.assertTrue(connectionConfiguration.accept("azerty"));
        Assert.assertFalse(connectionConfiguration.accept("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a_or_A() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:(?i)a.*");
        Assert.assertTrue(connectionConfiguration.accept("azerty"));
        Assert.assertTrue(connectionConfiguration.accept("Azerty"));
        Assert.assertFalse(connectionConfiguration.accept("bzerty"));
    }

    @Test
    public void should_accept_any_names_containing_with_ert() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:.*ert.*");
        Assert.assertTrue(connectionConfiguration.accept("azerty"));
        Assert.assertTrue(connectionConfiguration.accept("Azerty"));
        Assert.assertFalse(connectionConfiguration.accept("bzeprty"));
    }

    @Test
    public void should_accept_any_names_containing_with_names_split_by_comma() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("cautions*,fxent,Hermes");
        Assert.assertTrue(connectionConfiguration.accept("cautions"));
        Assert.assertTrue(connectionConfiguration.accept("fxent"));
        Assert.assertFalse(connectionConfiguration.accept("fxent-it"));
        Assert.assertTrue(connectionConfiguration.accept("cautions-it"));
        Assert.assertFalse(connectionConfiguration.accept("bzeprty"));
        Assert.assertTrue(connectionConfiguration.accept("hermes"));
    }

}
