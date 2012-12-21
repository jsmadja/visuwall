'use strict';

Object.prototype.getName = function() {
  var funcNameRegex = /function (.{1,})\(/;
  var results = (funcNameRegex).exec((this).constructor.toString());
  return (results && results.length > 1) ? results[1] : "";
};

function BuildsCtrl($scope, Builds, $timeout, $rootScope, $routeParams) {

  if($routeParams.wall) {
    $rootScope.wall = $routeParams.wall;
  } else {
    $rootScope.wall = "default";
  }

  var timeout = 1;

  function updateTime() {
    Builds.list({"wallName":$rootScope.wall}, function(remoteBuilds) {

      $rootScope.buildCount = builds.length;


      var remoteBuildsCount = 0;
      var currentBuildsCount = 0;
      if(remoteBuilds) {
        remoteBuildsCount = remoteBuilds.length;
      }
      if($scope.builds) {
        currentBuildsCount = $scope.builds.length;
      }

      var somethingChanged = remoteBuildsCount != currentBuildsCount;
      if(!somethingChanged) {
        for(var i=0;i<remoteBuilds.length;i++) {
          if($scope.builds[i].name != remoteBuilds[i].name) {
            somethingChanged = true;
            break;
          }
        }
      }

      if(somethingChanged) {
        $scope.builds = remoteBuilds;
      }
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
