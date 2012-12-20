'use strict';

angular.module('visuwallServices', ['ngResource']).
  factory('Builds',
  function ($resource) {
    return $resource('rest/walls/:wallName/builds/:name', {}, {
      list:{ method:'GET', isArray:true},
      get:{ method:'GET'}
    });
  }).
  factory('Analyses',
  function ($resource) {
    return $resource('rest/walls/:wallName/analyses/:name', {}, {
      list:{ method:'GET', isArray:true},
      get:{ method:'GET'}
    });
  }).
  factory('Tracks',
  function ($resource) {
    return $resource('rest/walls/:wallName/tracks/:name', {}, {
      list:{ method:'GET', isArray:true},
      get:{ method:'GET'}
    });
  }).
  factory('Walls',
  function ($resource) {
    return $resource('rest/walls', {}, {
      list:{ method:'GET', isArray:true }
    });
  }).
  factory('Connections',
  function ($resource) {
    return $resource('rest/walls/:wallName/connections/:name', {}, {
      list:{ method:'GET', isArray:true},
      update:{ method:'PUT', isArray:false}
    });
  }).
  factory('Connection', function ($resource) {
    return $resource('rest/connection', {}, {
    });
  }
);
