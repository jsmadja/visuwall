'use strict';

angular.module('visuwallServices', ['ngResource']).
    factory('Builds', function($resource) {
        return $resource('rest/walls/builds/:name', {}, {
            list: { method:'GET',   isArray:true},
            get:  { method:'GET'}
        });
    }).
    factory('Walls', function($resource) {
        return $resource('rest/walls', {}, {
            list: { method: 'GET',  isArray: true }
        });
    }).
    factory('Connections', function($resource) {
        return $resource('rest/walls/connections/:name', {}, {
            list: { method:'GET', isArray: true},
            update: { method: 'PUT', isArray: true },
        });
    }).
    factory('Connection', function($resource) {
        return $resource('rest/connection', {}, {
        });
    }
);
