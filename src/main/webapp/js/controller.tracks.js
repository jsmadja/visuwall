function TracksCtrl($scope, Tracks, $timeout, $rootScope) {

  var timeout = 1;

  function updateTime() {
    $scope.tracks = Tracks.list({"wallName":$rootScope.wall});
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