package com.visuwall.plugin.jenkins;

import com.visuwall.api.domain.SoftwareProjectId;
import com.visuwall.api.domain.build.TestResult;
import org.fest.assertions.Assertions;
import org.junit.Test;

public class JenkinsConnectionIT {

    @Test
    public void should_get_exact_test_count() {

        JenkinsConnection jenkinsConnection = new JenkinsConnection();
        jenkinsConnection.connect("http://jenkins-master", null, null);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("Fxent-development");
        TestResult testResult = jenkinsConnection.analyzeUnitTests(softwareProjectId);

        Assertions.assertThat(testResult.getPassCount()).isEqualTo(534);

    }

}
