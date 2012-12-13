'use strict';

function BuildsCtrl($scope,Builds,$timeout) {

    var timeout = 1;
    function updateTime() {
        $scope.builds = Builds.list();
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

function ConfigurationsCtrl($scope, $location, Walls, Connections) {

    $scope.connections = Connections.list();

    $scope.addConnection = function() {
        Connections.save($scope.connection, function(connection) {
            $location.path('/configurations');
        });
        $scope.connectionUrl = '';
    };

    $scope.updateConnection = function(connection) {
        Connections.update(connection, function() {
            $location.path('/configurations');
        });
    };

    $scope.removeConnection = function(connection) {
        Connections.delete({name: connection.name}, function() {
            $location.path('/configurations');
        });
    };

}