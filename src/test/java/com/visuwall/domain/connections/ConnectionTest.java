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

package com.visuwall.domain.connections;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectionTest {

    @Test
    public void should_accept_any_names_when_there_is_no_build_filter() throws Exception {
        Connection connectionConfiguration = new Connection();
        assertTrue(connectionConfiguration.accept("mybuild"));
    }

    @Test
    public void should_accept_any_names_starting_with_A() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:A.*");
        assertTrue(connectionConfiguration.accept("Azerty"));
        assertFalse(connectionConfiguration.accept("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:a.*");
        assertTrue(connectionConfiguration.accept("azerty"));
        assertFalse(connectionConfiguration.accept("bzerty"));
    }

    @Test
    public void should_accept_any_names_starting_with_a_or_A() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:(?i)a.*");
        assertTrue(connectionConfiguration.accept("azerty"));
        assertTrue(connectionConfiguration.accept("Azerty"));
        assertFalse(connectionConfiguration.accept("bzerty"));
    }

    @Test
    public void should_accept_any_names_containing_with_ert() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("regexp:.*ert.*");
        assertTrue(connectionConfiguration.accept("azerty"));
        assertTrue(connectionConfiguration.accept("Azerty"));
        assertFalse(connectionConfiguration.accept("bzeprty"));
    }

    @Test
    public void should_accept_any_names_containing_with_names_split_by_comma() throws Exception {
        Connection connectionConfiguration = new Connection();
        connectionConfiguration.setBuildFilter("cautions*,fxent,Hermes");
        assertTrue(connectionConfiguration.accept("cautions"));
        assertTrue(connectionConfiguration.accept("fxent"));
        assertFalse(connectionConfiguration.accept("fxent-it"));
        assertTrue(connectionConfiguration.accept("cautions-it"));
        assertFalse(connectionConfiguration.accept("bzeprty"));
        assertTrue(connectionConfiguration.accept("hermes"));
    }

}
