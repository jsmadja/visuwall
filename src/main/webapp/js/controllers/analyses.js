function AnalysesCtrl($scope, Analyses, $timeout, $rootScope, $routeParams) {

  updateCurrentWallFromUrl($routeParams, $rootScope);

  var timeout = 1;

  function updateTime() {
    $scope.analyses = Analyses.list({"wallName":$rootScope.wall}, function(remoteAnalyses) {
      $rootScope.analysisCount = remoteAnalyses.length;
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