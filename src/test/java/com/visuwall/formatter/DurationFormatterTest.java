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

package com.visuwall.formatter;

import org.junit.Test;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class DurationFormatterTest {

    @Test
    public void should_format() {
        long duration = HOURS.toMillis(3) + MINUTES.toMillis(3) + SECONDS.toMillis(3);
        DurationFormatter durationFormatter = new DurationFormatter(duration);
        assertEquals("3h3m3s", durationFormatter.toString());
    }

    @Test
    public void should_format_in_hours() {
        long duration = HOURS.toMillis(3);
        DurationFormatter durationFormatter = new DurationFormatter(duration);
        assertEquals("3h", durationFormatter.toString());
    }

    @Test
    public void should_format_in_minutes() {
        long duration = MINUTES.toMillis(3);
        DurationFormatter durationFormatter = new DurationFormatter(duration);
        assertEquals("3m", durationFormatter.toString());
    }

    @Test
    public void should_format_in_seconds() {
        long duration = SECONDS.toMillis(3);
        DurationFormatter durationFormatter = new DurationFormatter(duration);
        assertEquals("3s", durationFormatter.toString());
    }

}
