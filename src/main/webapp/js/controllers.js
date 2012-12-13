'use strict';

function BuildsCtrl($scope,Builds,$timeout) {

    $scope.setBuild = function(build, builds) {
        $scope.build = build;
        $scope.builds = builds;
    }

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

function ConfigurationCtrl($scope, $location, Wall, Connection) {

    $scope.connections = [
        {name:'visuwall demo', url:'http://demo.visuwall.ci'},
        {name:'my client', url:'http://jenkins-master', buildFilter:'cautions*,fxent*,hermes*'},
        {name:'awired', url:'http://ci.awired.net', buildFilter:'visuwall*'}
    ];

    $scope.addConnection = function() {
        Wall.save($scope.connection, function(connection) {
            $location.path('/builds');
        });
        $scope.connectionUrl = '';
    };

    $scope.updateConnection = function(connection) {
        Connection.update(connection, function() {
            $location.path('/configuration');
        });
    };

    $scope.removeConnection = function(connection) {
        Connection.remove({name: connection.name}, function() {
            $location.path('/configuration');
        });
    };

}