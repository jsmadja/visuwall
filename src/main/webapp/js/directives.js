'use strict';

angular.module('visuwallDirectives', ['ngResource'])
    .directive('refreshBuild', function($timeout, Build) {

        return function($scope, element, attrs) {

            var build, timeoutId;

            function updateTime() {
                var builds = $scope.builds;
                var name = $scope.build.name;
                for (var i=0; i < builds.length; i++) {
                    if(builds[i].name == name) {
                        Build.get({name: name}, function(remoteBuild) {
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

            $scope.$watch(attrs.build, function(value) {
                build = value;
                updateTime();
            });

            function updateLater() {
                timeoutId = $timeout(function() {
                    updateTime();
                    updateLater();
                }, 10*1000);
            }

            element.bind('$destroy', function() {
                $timeout.cancel(timeoutId);
            });

            updateLater();
        }
    });