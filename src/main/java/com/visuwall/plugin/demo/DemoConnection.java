package com.visuwall.plugin.demo;

import com.visuwall.api.domain.*;
import com.visuwall.api.domain.quality.QualityMeasure;
import com.visuwall.api.domain.quality.QualityMetric;
import com.visuwall.api.domain.quality.QualityResult;
import com.visuwall.api.exception.BuildIdNotFoundException;
import com.visuwall.api.exception.BuildNotFoundException;
import com.visuwall.api.exception.ProjectNotFoundException;
import com.visuwall.api.exception.ViewNotFoundException;
import com.visuwall.api.plugin.capability.BuildCapability;
import com.visuwall.api.plugin.capability.MetricCapability;
import com.visuwall.api.plugin.capability.TestCapability;
import com.visuwall.api.plugin.capability.ViewCapability;
import org.joda.time.DateTime;

import java.util.*;

import static com.visuwall.api.domain.BuildState.*;
import static com.visuwall.plugin.demo.SoftwareProjectIds.*;

public class DemoConnection implements BuildCapability, TestCapability, ViewCapability, MetricCapability {

    private Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>();
    private Map<SoftwareProjectId, BuildState> buildStates = new HashMap<SoftwareProjectId, BuildState>();
    private Map<SoftwareProjectId, QualityResult> qualityResults = new HashMap<SoftwareProjectId, QualityResult>();

    private List<String> views = new ArrayList<String>();

    private ChangeStateProject marsProj = new ChangeStateProject();

    private String url = "http://demo.visuwall.ci";

    public DemoConnection() {
        softwareProjectIds.put(moon, "Moon");
        softwareProjectIds.put(earth, "Earth");
        softwareProjectIds.put(mars, "Mars");
        softwareProjectIds.put(pluto, "Pluto");
        softwareProjectIds.put(neptune, "Neptune");
        softwareProjectIds.put(uranus, "Uranus");
        softwareProjectIds.put(saturn, "Saturn");
        softwareProjectIds.put(mercury, "Mercury");
        softwareProjectIds.put(venus, "Venus");

        buildStates.put(mars, FAILURE);
        buildStates.put(pluto, UNKNOWN);
        buildStates.put(uranus, SUCCESS);
        buildStates.put(neptune, SUCCESS);
        buildStates.put(saturn, UNSTABLE);
        buildStates.put(venus, UNSTABLE);
        buildStates.put(moon, SUCCESS);
        buildStates.put(earth, SUCCESS);
        buildStates.put(mercury, SUCCESS);

        views.add("Telluriques");
        views.add("Gazeuses");
        views.add("Other");

        TestResult saturnTestResults = createTestResult(78, 120, 10, 20);
        TestResult venusTestResults = createTestResult(25, 457, 3, 16);
        TestResult neptuneTestResults = createTestResult(90, 872, 0, 0);
        TestResult mercuryTestResults = createTestResult(78, 439, 0, 0);

        new HashMap<SoftwareProjectId, TestResult>().put(saturn, saturnTestResults);
        new HashMap<SoftwareProjectId, TestResult>().put(neptune, neptuneTestResults);
        new HashMap<SoftwareProjectId, TestResult>().put(mercury, mercuryTestResults);
        new HashMap<SoftwareProjectId, TestResult>().put(venus, venusTestResults);

        TestResult neptuneIntegrationTestResults = createTestResult(78, 163, 0, 0);
        TestResult mercuryIntegrationTestResults = createTestResult(89, 236, 0, 0);
        TestResult venusIntegrationTestResults = createTestResult(49, 178, 4, 2);

        new HashMap<SoftwareProjectId, TestResult>().put(neptune, neptuneIntegrationTestResults);
        new HashMap<SoftwareProjectId, TestResult>().put(mercury, mercuryIntegrationTestResults);
        new HashMap<SoftwareProjectId, TestResult>().put(venus, venusIntegrationTestResults);

        QualityMeasure uranusCoverageMeasure = createQualityMeasure("coverage", "Coverage", "76.5 %", 76.5, -1);
        QualityMeasure uranusLocMeasure = createQualityMeasure("ncloc", "Lines of code", "78.001", 78001D, 1);
        QualityMeasure uranusViolationsMeasure = createQualityMeasure("violations_density", "Violations", "32", 32D, -1);
        QualityResult uranusQualityResult = new QualityResult();
        uranusQualityResult.add("coverage", uranusCoverageMeasure);
        uranusQualityResult.add("ncloc", uranusLocMeasure);
        uranusQualityResult.add("violations_density", uranusViolationsMeasure);

        QualityResult mercuryQualityResult = new QualityResult();
        QualityMeasure mercuryLocMeasure = createQualityMeasure("ncloc", "Lines of code", "121.988", 121988D, 1);
        mercuryQualityResult.add("ncloc", mercuryLocMeasure);

        qualityResults.put(uranus, uranusQualityResult);
        qualityResults.put(mercury, mercuryQualityResult);

        new ArrayList<String>().add("1");
    }

    private QualityMeasure createQualityMeasure(String key, String name, String formattedValue, double value, int tendency) {
        QualityMeasure coverageMeasure = new QualityMeasure();
        coverageMeasure.setKey(key);
        coverageMeasure.setName(name);
        coverageMeasure.setFormattedValue(formattedValue);
        coverageMeasure.setValue(value);
        coverageMeasure.setTendency(tendency);
        return coverageMeasure;
    }

    private TestResult createTestResult(int coverage, int passCount, int failCount, int skipCount) {
        TestResult saturnTestResults = new TestResult();
        saturnTestResults.setCoverage(coverage);
        saturnTestResults.setFailCount(failCount);
        saturnTestResults.setSkipCount(skipCount);
        saturnTestResults.setPassCount(passCount);
        return saturnTestResults;
    }

    @Override
    public void connect(String url, String login, String password) {
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        return softwareProjectIds;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        String name = projectId.getProjectId();
        String firstLetter = "" + name.charAt(0);
        return firstLetter.toUpperCase() + name.substring(1);
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Map<String, List<QualityMetric>> getMetricsByCategory() {
        return new HashMap<String, List<QualityMetric>>();
    }

    @Override
    public QualityResult analyzeQuality(SoftwareProjectId projectId, String... metrics) {
        return qualityResults.get(projectId);
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        if (views.contains("Telluriques")) {
            softwareProjectIds.add(earth);
        }
        return softwareProjectIds;
    }

    @Override
    public List<String> findViews() {
        return views;
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        List<String> projectNames = new ArrayList<String>();
        if ("Telluriques".equals(viewName)) {
            projectNames.add("Earth");
        }
        return projectNames;
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        TestResult testResult = new HashMap<SoftwareProjectId, TestResult>().get(projectId);
        if (testResult == null) {
            testResult = new TestResult();
        }
        return testResult;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        List<Commiter> commiters = new ArrayList<Commiter>();
        if (softwareProjectId.equals(mars)) {
            return marsProj.getCommiters(buildId);
        }
        return commiters;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        if (softwareProjectId.equals(mars)) {
            return marsProj.getBuildTime(buildId);
        }
        BuildTime buildTime = new BuildTime();
        int milisDuration = randomDuration();
        buildTime.setDuration(milisDuration);
        Date startDate = randomPastDate();
        buildTime.setStartTime(startDate);
        return buildTime;
    }

    private Date randomPastDate() {
        int minutesAgo = (int) (Math.random() * 50);
        return new DateTime().minusHours(minutesAgo).toDate();
    }

    private int randomDuration() {
        return (int) (Math.random() * 5000) * 60;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        BuildState buildState = buildStates.get(projectId);
        if (buildState == null) {
            throw new ProjectNotFoundException("Cannot find project for " + projectId);
        }
        if (mars.equals(projectId)) {
            return marsProj.getBuildState(buildId);
        }
        return buildState;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        if (projectId.equals(mars)) {
            return marsProj.isBuilding();
        }
        if (projectId.equals(moon)) {
            return true;
        }
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        String lastBuildId = "1";
        if (softwareProjectId.equals(mars)) {
            return marsProj.getLastBuildId();
        }
        return lastBuildId;
    }

    @Override
    public String toString() {
        return "Demo Connection";
    }

}
