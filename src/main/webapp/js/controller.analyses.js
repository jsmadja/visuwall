function AnalysesCtrl($scope, Analyses, $timeout, $rootScope) {

  var timeout = 1;

  function updateTime() {
    $scope.analyses = Analyses.list({"wallName":$rootScope.wall});
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