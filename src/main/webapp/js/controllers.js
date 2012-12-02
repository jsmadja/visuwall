'use strict';

/* Controllers */

function ProjectsCtrl($scope,Project,$timeout) {
    var timeout = 1;
    function updateTime() {
        $scope.projects = Project.query();
    }
    function updateLater() {
        $timeout(function() {
            updateTime();
            updateLater();
            timeout = 600*1000;
        }, timeout);
    }
    updateLater();
}

function TracksCtrl($scope, Track) {
    $scope.tracks = Track.query();
}

function MetricsCtrl($scope, Metric) {
    $scope.metrics = Metric.query();
}

function DeploymentsCtrl($scope, Deployment) {
    $scope.deployments = Deployment.query();
}