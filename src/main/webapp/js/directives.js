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
  })
  .directive('refreshTrack', function ($timeout, Tracks) {
    return function ($scope, element, attrs) {

      var track, timeoutId;

      function updateTime() {
        var tracks = $scope.tracks;
        var name = $scope.track.name;
        for (var i = 0; i < tracks.length; i++) {
          if (tracks[i].name == name) {
            console.log(new Date()+"Time to update track "+name);
            Tracks.get({name:name}, function (remoteTrack) {
              var trackToUpdate = tracks[i];
              trackToUpdate.name = remoteTrack.name;
              trackToUpdate.actualVelocity = remoteTrack.actualVelocity;
              trackToUpdate.daysToGo = remoteTrack.daysToGo;
              trackToUpdate.acceptedStories = remoteTrack.acceptedStories;
              trackToUpdate.storiesInProgress = remoteTrack.storiesInProgress;
              trackToUpdate.availableStories = remoteTrack.availableStories;
              trackToUpdate.numberOfSprints = remoteTrack.numberOfSprints;
              trackToUpdate.remainingStories = remoteTrack.remainingStories;
              trackToUpdate.storiesInValidation = remoteTrack.storiesInValidation;
              trackToUpdate.scheduledStories = remoteTrack.scheduledStories;
              trackToUpdate.waitingForEstimationStories = remoteTrack.waitingForEstimationStories;
            });
            return;
          }
        }
      }

      $scope.$watch(attrs.build, function (value) {
        track = value;
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