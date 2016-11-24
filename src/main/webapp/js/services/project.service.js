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
        var endpoint="/timekeeper/svc/project";
        
        /**
         * Get project by id
         */
        service.getProject=function(id){
            return $http.get(endpoint+'/'+id);
        };

        /**
         * Get task by project id
         */
        service.getTaks=function(id){
            return $http.get(endpoint+'/'+id+"/tasks");
        };

        /**
         * get task info by project id and task id
         */
        service.getTask=function(projectId,taskId){
            return $http.get(endpoint+'/'+projectId+"/task/"+taskId);
        }
        
        service.all=function(enabled){
            return $http.get(endpoint+'/list?e='+enabled);
        };

        return service;
    }
})();