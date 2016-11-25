(function(){
    'use strict';
    /**
     * 
    */
    var timekeeperControllers = angular.module("timekeeperControllers");

    timekeeperControllers.controller("timecard_list_ctrl", function($scope, $log, $http, $routeParams,timecardService) {
        $scope.loading = true;
        /*$http.get('/timekeeper/svc/timecard/list?').
            success(function(data) {
                $scope.timecards = data;
                $scope.loading = false;
            }).
            error(function(data) {
                $scope.timecards = data;
                $scope.loading = false;
            });*/
        timecardService.getAllByPm(1).then(
            function(response){
                $log.debug("recebeu timecards ");
                $log.debug(reponse);
                $scope.timecards=response.data;
                $scope.loading = false;
            },function(error){
                $log.debug("An error has occured "+error.data);
                $scope.timecards = data;
                $scope.loading = false;
            }
        )
        
    });
})();