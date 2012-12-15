'use strict';

angular.module('visuwall', ['visuwallServices', 'visuwallDirectives']).
  config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/builds', {templateUrl:'partials/builds.html', controller:BuildsCtrl}).
    //when('/tracks',        {templateUrl: 'partials/tracks.html',         controller:TracksCtrl}).
    when('/analyses', {templateUrl:'partials/analyses.html', controller:AnalysesCtrl}).
    //when('/deployments',   {templateUrl: 'partials/deployments.html',    controller:DeploymentsCtrl}).
    when('/configurations', {templateUrl:'partials/configurations.html', controller:ConfigurationsCtrl}).
    otherwise({redirectTo:'/builds'});
}]);