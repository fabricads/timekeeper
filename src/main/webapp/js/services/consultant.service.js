(function(){
    'use strict';
    /** Services Module */
    var servicesApp = angular.module("servicesApp");

    /** Project Service 
     *  responsable for the communication with the project rest endpoint
     */
    servicesApp.factory("consultantService",consultantService);
    function consultantService( $http){
        var service = {};
        var endpoint="svc/person/consultant-list";
        
        /**
         * Get project by id
         */
        service.getAll=function(){
            return $http.get(endpoint);
        };
        

        return service;
    }
})();