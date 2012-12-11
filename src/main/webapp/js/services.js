'use strict';

/* Services */

angular.module('visuwallServices', ['ngResource']).
    factory('Build',
    function($resource) {
        return $resource('rest/builds/:buildName', {}, {query: {method:'GET', params:{buildName:''}, isArray:true} });
    }).
    factory('Builds',
    function($resource) {
        return $resource('rest/builds', {}, {query: {method:'GET', params:{buildName:''}, isArray:true} });
    }).
    factory('Track',
    function($resource){
        return $resource('data/:trackId.json', {}, { query: {method:'GET', params:{trackId:'tracks'}, isArray:true}});
    }).
    factory('Metric',
    function($resource){
        return $resource('data/:metricId.json', {}, { query: {method:'GET', params:{metricId:'metrics'}, isArray:true}});
    }).
    factory('Deployment',
    function($resource){
        return $resource('data/:deploymentId.json', {}, { query: {method:'GET', params:{deploymentId:'deployments'}, isArray:true}});
    }).
    factory('Wall', function($resource) {
        var Wall = $resource('rest/walls',
            {  }, {
                update: { method: 'PUT' }
            }
        );
        return Wall;
    });