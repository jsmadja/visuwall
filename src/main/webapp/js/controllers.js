'use strict';

/* Controllers */

function BuildsCtrl($scope,Build,$timeout) {
    var timeout = 1;
    function updateTime() {
        $scope.builds = Build.query();
    }
    function updateLater() {
        $timeout(function() {
            updateTime();
            updateLater();
            timeout = 10*1000;
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
        {url:'http://ci.awired.net/jenkins/'}
    ];

       // $scope.connections.push({url:$scope.connectionUrl});
    $scope.addConnection = function() {
        Wall.save($scope.connection, function(connection) {
            $location.path('/builds');
        });
        $scope.connectionUrl = '';
    };

}