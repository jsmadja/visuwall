function TracksCtrl($scope, Tracks, $timeout, $rootScope, $routeParams) {

  if($routeParams.wall) {
    $rootScope.wall = $routeParams.wall;
  } else {
    $rootScope.wall = "default";
  }

  var timeout = 1;

  function updateTime() {
    $scope.tracks = Tracks.list({"wallName":$rootScope.wall}, function (remoteTracks) {

      $rootScope.trackCount = remoteTracks.length;

    });
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