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
            update: { method: 'PUT', isArray: true },
            remove: { method: 'DELETE'}
        });
    }
);
