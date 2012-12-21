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

package com.visuwall.domain.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginConfiguration {
    public static PluginConfiguration noConfiguration = new PluginConfiguration();

    private Map<String, String> values = new HashMap<String, String>();

    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(values);
    }

    public String get(String key) {
        return values.get(key);
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public boolean hasValueFor(String key) {
        return values.containsKey(key);
    }

    public boolean hasPassword() {
        return hasValueFor("password");
    }
}
