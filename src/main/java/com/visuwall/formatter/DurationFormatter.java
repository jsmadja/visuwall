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