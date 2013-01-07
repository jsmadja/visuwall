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

package com.visuwall.plugin.demo.test;

import com.visuwall.api.domain.*;
import com.visuwall.api.domain.build.TestResult;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.plugin.demo.DemoConnection;
import com.visuwall.plugin.demo.build.DemoBuildConnection;

public class DemoTestConnection extends DemoBuildConnection implements TestCapability {

    public DemoTestConnection() {
    }

    @Override
    public String toString() {
        return "Demo Test Connection";
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        TestResult testResult = new TestResult();
        testResult.setFailCount(9708);
        testResult.setPassCount(1234);
        testResult.setSkipCount(6368);
        return testResult;
    }
}
