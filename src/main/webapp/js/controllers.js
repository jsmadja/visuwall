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

function ConfigurationsCtrl($scope, $location, Connection, Connections) {

    $scope.connections = Connections.list();

    $scope.addConnection = function() {
        $scope.connection.includeBuildNames = $scope.buildNames;
        Connections.save($scope.connection, function(connection) {
            $location.path('/configurations');
        });
    };

    $scope.updateConnection = function(connection) {
        $scope.connection = connection;
    };

    $scope.removeConnection = function(connection) {
        Connections.delete({name: connection.name}, function() {
            $location.path('/configurations');
        });
    };

    $scope.checkConnection = function() {
        var url = $scope.connection.url;
        setTimeout(function() {
            if ($scope.connection.url == url) {
                Connection.save($scope.connection, function(connection) {
                    $scope.connection = connection;
                });
            }
            url = $scope.connection.url;
        }, 500);
    }

    $scope.applyFilter = function() {
        var buildFilter = $scope.connection.buildFilter;
        setTimeout(function() {
            if ($scope.connection.buildFilter == buildFilter) {
                Connection.save($scope.connection, function(connection) {
                    $scope.connection = connection;
                });
            }
            buildFilter = $scope.connection.buildFilter;
        }, 500);
    }

}