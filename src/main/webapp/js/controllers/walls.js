function WallsCtrl($scope, $rootScope, Walls, $location, Builds, $routeParams) {

  if($routeParams.wall) {
    $rootScope.wall = $routeParams.wall;
  } else {
    $rootScope.wall = "default";
  }

  $scope.walls = Walls.list();

  $scope.getWallNameFromUrl = function ($routeParams) {
    if($routeParams.wall) {
      return $routeParams.wall;
    }
    return "default";
  };

  $scope.setCurrentWall = function (name) {
    $rootScope.wall = name;
  };

  $scope.deleteWall = function (wall) {
    Walls.delete({"wallName": wall}, function () {
      $scope.walls = Walls.list();
      $location.path('/');
    });
  };

}