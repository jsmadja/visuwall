function WallsCtrl($scope, $rootScope, Walls) {

  $scope.walls = Walls.list();

  $scope.getWallNameFromUrl = function ($routeParams) {
    if($routeParams.wall) {
      return $routeParams.wall;
    }
    return "default";
  }

  $scope.setCurrentWall = function (name) {
    $rootScope.wall = name;
  }

}