package com.visuwall.formatter;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DurationFormatter {
    private long duration;
    public DurationFormatter(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix("h", "h")
                .appendMinutes()
                .appendSuffix("m", "m")
                .appendSeconds()
                .appendSuffix("s", "s")
                .toFormatter();
        Period period = new Period(duration);
        return daysHoursMinutes.print(period.normalizedStandard());
    }
}