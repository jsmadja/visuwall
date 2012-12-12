'use strict';

/* Controllers */

function BuildCtrl($scope) {
    $scope.setBuild = function(build, builds) {
        $scope.build = build;
        $scope.builds = builds;
    }
}

function BuildsCtrl($scope,Build,$timeout) {
    var timeout = 1;
    function updateTime() {
        $scope.builds = Build.query();
    }
    function updateLater() {
        $timeout(function() {
            updateTime();
            updateLater();
            if($scope.builds == null) {
                timeout = 5*1000;
            } else {
                timeout = 60*1000;
            }
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

function ConfigurationCtrl($scope, $location, Wall) {

    $scope.connections = [
        {url:'http://demo.visuwall.ci'}
    ];

    $scope.addConnection = function() {
        Wall.save($scope.connection, function(connection) {
            $location.path('/builds');
        });
        $scope.connectionUrl = '';
    };

}