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

package com.visuwall;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect Chrono {

    private static final Logger LOG = LoggerFactory.getLogger(Chrono.class);

    Object around() : execution(public * com.visuwall.domain.*.* (..)) {
        long start = System.currentTimeMillis();
        try {
            return proceed();
        } finally {
        	String prefix = "";
            long end = System.currentTimeMillis();
            long duration = end - start;
            Object method = thisJoinPointStaticPart.getSignature();

            if (duration > 10) {
            	prefix = "[SLOW] ";
            	Object[] args = thisJoinPoint.getArgs();
                LOG.warn("Chronometer "+prefix+" "+method+" "+Arrays.toString(args)+", "+duration+" ms");
            }
        }
    }
    
}
