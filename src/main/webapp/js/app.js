'use strict';

angular.module('visuwall', ['visuwallServices', 'visuwallDirectives']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/:wall/builds',        {templateUrl:'partials/builds.html',   controller:BuildsCtrl}).
    when('/:wall/tracks',        {templateUrl: 'partials/tracks.html',  controller:TracksCtrl}).
    when('/:wall/analyses',      {templateUrl:'partials/analyses.html', controller:AnalysesCtrl}).
    //when('/deployments',   {templateUrl: 'partials/deployments.html',    controller:DeploymentsCtrl}).
    when('/:wall/configurations', {templateUrl:'partials/configurations.html', controller:ConfigurationsCtrl}).
    otherwise({redirectTo:'/default/builds'});
}]);