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

package com.visuwall.domain;

import com.visuwall.domain.connections.Connection;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public interface Refreshables<T extends Refreshable> {

    void addConnection(Connection connection);

    void removeAllFrom(Connection connection);

    int count();

    boolean contains(String name);

    List<T> all();

    void refresh(ExecutorService pool);

}
