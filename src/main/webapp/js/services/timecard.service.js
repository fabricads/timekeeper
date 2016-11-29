(function(){
    'use strict';
    /** Services Module */
    var servicesApp = angular.module("servicesApp");

    /** Project Service 
     *  responsable for the communication with the project rest endpoint
     */
    servicesApp.factory("timecardService",timecardService);
    function timecardService( $http){
        var service = {};
        var endpoint="/timekeeper/svc/timecard";
        
        /**
         * Get project by id
         */
        service.getAllByPm=function(pm){
            return $http.get(endpoint+"/list?pm="+pm);
        };
        
        /**
         * Get project
         */
        service.get= function(id){
            return $http.get("/timekeeper/svc/project/"+id+"/tc");
            
        };

        return service;
    }
})();