'use strict';

Object.prototype.getName = function() {
  var funcNameRegex = /function (.{1,})\(/;
  var results = (funcNameRegex).exec((this).constructor.toString());
  return (results && results.length > 1) ? results[1] : "";
};

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

function TracksCtrl($scope, Tracks, $timeout) {

  var timeout = 1;

  function updateTime() {
    $scope.tracks = Tracks.list();
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
      $scope.connections = Connections.list();
    });
  };

  $scope.editConnection = function (connection) {
    $scope.connection = connection;
  };

  $scope.removeConnection = function (connection) {
    Connections.delete({name:connection.name}, function () {
      $scope.connections = Connections.list();
      $location.path('/configurations');
    });
  };

  $scope.checkConnection = function () {
    var url = $scope.connection.url;
    var login = $scope.connection.login;
    var password = $scope.connection.password;
    var name = $scope.connection.name;

    setTimeout(function () {
      var noNewUrl = $scope.connection.url == url;
      var noNewLogin = $scope.connection.login == login;
      var noNewPassword = $scope.connection.password == password;
      var nothingChanged = noNewUrl && noNewLogin && noNewPassword;
      if (nothingChanged) {
        Connection.save($scope.connection, function (connection) {
          $scope.connection = connection;
          $scope.connection.password = password;
          $scope.connection.name = name;
        });
      }
      url = $scope.connection.url;
      login = $scope.connection.login;
      password = $scope.connection.password;
      name = $scope.connection.name;
    }, 1000);
  };

  $scope.applyFilter = function () {
    var buildFilter = $scope.connection.buildFilter;
    var password = $scope.connection.password;
    setTimeout(function () {
      if ($scope.connection.buildFilter == buildFilter) {
        Connection.save($scope.connection, function (connection) {
          $scope.connection = connection;
          $scope.connection.password = password;
        });
      }
      buildFilter = $scope.connection.buildFilter;
      password = $scope.connection.password;
    }, 500);
  }

}