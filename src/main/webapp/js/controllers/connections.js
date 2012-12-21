function ConfigurationsCtrl($scope, $location, Connection, Connections, $rootScope, $routeParams) {

  if($routeParams.wall) {
    $rootScope.wall = $routeParams.wall;
  } else {
    $rootScope.wall = "default";
  }

  $scope.connections = Connections.list({"wallName": $rootScope.wall});

  $scope.addConnection = function () {
    $scope.connection.includeBuildNames = $scope.buildNames;
    $scope.connection.includeMetricNames = $scope.metricNames;
    Connections.update({wallName: $rootScope.wall}, $scope.connection, function (connection) {
      $location.path($rootScope.wall+'/configurations');
      $scope.connections = Connections.list({"wallName":$rootScope.wall});
    });
  };

  $scope.editConnection = function (connection) {
    $scope.connection = connection;
  };

  $scope.removeConnection = function (connection) {
    Connections.delete({"wallName": $rootScope.wall, "name":connection.name}, function () {
      $scope.connections = Connections.list({"wallName": $rootScope.wall});
      $location.path($rootScope.wall+'/configurations');
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