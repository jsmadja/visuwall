'use strict';

angular.module('visuwall', ['visuwallServices']).
  config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/builds',        {templateUrl: 'partials/builds.html',       controller:BuildsCtrl}).
      when('/tracks',        {templateUrl: 'partials/tracks.html',         controller:TracksCtrl}).
      when('/metrics',       {templateUrl: 'partials/metrics.html',        controller:MetricsCtrl}).
      when('/deployments',   {templateUrl: 'partials/deployments.html',    controller:DeploymentsCtrl}).
      when('/configuration', {templateUrl: 'partials/configuration.html',    controller:ConfigurationCtrl}).
      otherwise({redirectTo: '/builds'});
}]).directive('refreshBuild', function($timeout, Build, Builds) {

    return function($scope, element, attrs) {

        var build, timeoutId;

        function updateTime() {
            var builds = $scope.builds;
            var buildName = $scope.build.name;
            for (var i=0; i < builds.length; i++) {
                if(builds[i].name == buildName) {
                    Build.get({buildName: buildName}, function(remoteBuild) {
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