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

package com.visuwall.plugin.bamboo;

import com.visuwall.api.domain.build.BuildState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<String, BuildState> STATE_MAPPING = new HashMap<String, BuildState>();

    static {
        STATE_MAPPING.put("Successful", BuildState.SUCCESS);
        STATE_MAPPING.put("Failed", BuildState.FAILURE);
    }

    public static BuildState asVisuwallState(String bambooState) {
        BuildState state = STATE_MAPPING.get(bambooState);
        if (state == null) {
            state = BuildState.UNKNOWN;
            LOG.warn(bambooState + " is not available in Bamboo plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }
}
