'use strict';

/* Services */

angular.module('visuwallServices', ['ngResource']).
    factory('Build', function($resource) {
        return $resource('rest/walls/builds/:name', {}, {
        });
    }).
    factory('Builds', function($resource) {
        return $resource('rest/walls/builds', {}, {
            list: { method:'GET',   isArray:true}
        });
    }).
    factory('Wall', function($resource) {
        return $resource('rest/walls', {}, {
            list: { method: 'GET',  isArray: true }
        });
    }).
    factory('Connection', function($resource) {
       return $resource('rest/walls/connections/:name', {}, {
           update: { method: 'PUT', isArray: true },
           remove: { method: 'DELETE'}
       });
    }
);