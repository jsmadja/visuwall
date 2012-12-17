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

package com.visuwall.plugin.teamcity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

class DateAdapter {

    private static final SimpleDateFormat TEAMCITY_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

    private static final Logger LOG = LoggerFactory.getLogger(DateAdapter.class);

    private DateAdapter() {
    }

    static Date parseDate(String dateToParse) {
        try {
            return TEAMCITY_DATE_FORMAT.parse(dateToParse);
        } catch (Throwable t) {
            LOG.warn("Cannot parse date: " + dateToParse, t);
            return new Date();
        }
    }

}
