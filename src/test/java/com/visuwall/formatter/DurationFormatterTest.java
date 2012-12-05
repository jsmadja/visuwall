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
