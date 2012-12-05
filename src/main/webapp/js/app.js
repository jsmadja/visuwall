'use strict';

/* App Module */

angular.module('visuwall', ['visuwallServices']).
  config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/builds',      {templateUrl: 'partials/builds.html',       controller:BuildsCtrl}).
      when('/tracks',        {templateUrl: 'partials/tracks.html',         controller:TracksCtrl}).
      when('/metrics',       {templateUrl: 'partials/metrics.html',        controller:MetricsCtrl}).
      when('/deployments',   {templateUrl: 'partials/deployments.html',    controller:DeploymentsCtrl}).
      otherwise({redirectTo: '/builds'});
}]);
