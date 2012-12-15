'use strict';

angular.module('visuwallDirectives', ['ngResource'])
  .directive('refreshBuild', function ($timeout, Builds) {
    return function ($scope, element, attrs) {

      var build, timeoutId;

      function updateTime() {
        var builds = $scope.builds;
        var name = $scope.build.name;
        for (var i = 0; i < builds.length; i++) {
          if (builds[i].name == name) {
            console.log(new Date()+"Time to update build "+name);
            Builds.get({name:name}, function (remoteBuild) {
              var buildToUpdate = builds[i];
              buildToUpdate.name = remoteBuild.name;
              buildToUpdate.status = remoteBuild.status;
              buildToUpdate.duration = remoteBuild.duration;
              buildToUpdate.lastBuildDate = remoteBuild.lastBuildDate;
              buildToUpdate.successfulTestCount = remoteBuild.successfulTestCount;
              buildToUpdate.failedTestCount = remoteBuild.failedTestCount;
              buildToUpdate.skippedTestCount = remoteBuild.skippedTestCount;
            });
            return;
          }
        }
      }

      $scope.$watch(attrs.build, function (value) {
        build = value;
        updateTime();
      });

      function updateLater() {
        timeoutId = $timeout(function () {
          updateTime();
          updateLater();
        }, 10 * 1000);
      }

      element.bind('$destroy', function () {
        $timeout.cancel(timeoutId);
      });

      updateLater();
    }
  })
  .directive('refreshAnalysis', function ($timeout, Analyses) {
    return function ($scope, element, attrs) {

      var analysis, timeoutId;

      function updateTime() {
        var analyses = $scope.analyses;
        var name = $scope.analysis.name;
        for (var i = 0; i < analyses.length; i++) {
          if (analyses[i].name == name) {
              console.log(new Date()+"Time to update analysis "+name);
              Analyses.get({name:name}, function (remoteAnalysis) {
              var analysisToUpdate = analyses[i];
              analysisToUpdate.name = remoteAnalysis.name;
              analysisToUpdate.metrics = remoteAnalysis.metrics;
            });
            return;
          }
        }
      }

      $scope.$watch(attrs.build, function (value) {
        analysis = value;
        updateTime();
      });

      function updateLater() {
        timeoutId = $timeout(function () {
          updateTime();
          updateLater();
        }, 10 * 1000);
      }

      element.bind('$destroy', function () {
        $timeout.cancel(timeoutId);
      });

      updateLater();
    }
  });