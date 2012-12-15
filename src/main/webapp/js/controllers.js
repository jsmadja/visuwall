'use strict';

function BuildsCtrl($scope, Builds, $timeout) {

  var timeout = 1;

  function updateTime() {
    $scope.builds = Builds.list();
  }

  function updateLater() {
    $timeout(function () {
      updateTime();
      updateLater();
      timeout = 60 * 1000;
    }, timeout);
  }

  updateLater();
}

function AnalysesCtrl($scope, Analyses, $timeout) {

  var timeout = 1;

  function updateTime() {
    $scope.analyses = Analyses.list();
  }

  function updateLater() {
    $timeout(function () {
      updateTime();
      updateLater();
      timeout = 60 * 1000;
    }, timeout);
  }

  updateLater();
}


function ConfigurationsCtrl($scope, $location, Connection, Connections) {

  $scope.connections = Connections.list();

  $scope.addConnection = function () {
    $scope.connection.includeBuildNames = $scope.buildNames;
    $scope.connection.includeMetricNames = $scope.metricNames;
    Connections.update($scope.connection, function (connection) {
      $location.path('/configurations');
    });
  };

  $scope.editConnection = function (connection) {
    $scope.connection = connection;
  };

  $scope.removeConnection = function (connection) {
    Connections.delete({name:connection.name}, function () {
      $location.path('/configurations');
    });
  };

  $scope.checkConnection = function () {
    var url = $scope.connection.url;
    setTimeout(function () {
      if ($scope.connection.url == url) {
        Connection.save($scope.connection, function (connection) {
          $scope.connection = connection;
        });
      }
      url = $scope.connection.url;
    }, 1000);
  };

  $scope.applyFilter = function () {
    var buildFilter = $scope.connection.buildFilter;
    setTimeout(function () {
      if ($scope.connection.buildFilter == buildFilter) {
        Connection.save($scope.connection, function (connection) {
          $scope.connection = connection;
        });
      }
      buildFilter = $scope.connection.buildFilter;
    }, 500);
  }

}