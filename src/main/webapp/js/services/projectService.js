(function(){
        'use strict';
        /** Services Module */
        var servicesApp = angular.module("servicesApp");
        /** Project Service 
         *  responsable for the communication with the project rest endpoint
         */
        servicesApp.factory("projectService",projectService);
        function projectService( $http){
            var service = {};
            service.all=function(enabled){
            	console.log("retriving!!");
                return $http.get('/timekeeper/svc/project/list?e='+enabled);
            };
            return service;
        }
})();