var timecardApp = angular.module("timecard_ctrl", [ "ngRoute", "ngResource", "ui.bootstrap", "servicesApp", "timekeeperApp" ]);

/* ********************************************************
 * 
 * Timecard controllers
 * 
 * ********************************************************
 */

timecardApp.controller("show_modal_select_project", function($scope, $modal) {

    $scope.select_project = function () {
        
        var modalInstance = $modal.open({
          templateUrl: 'modal_select_project.html',
          controller: 'modal_instance'
        });
        
      };
});

timecardApp.controller("modal_instance", function($rootScope, $scope, $http, $window, $modalInstance) {

    $scope.timecard = {};
    
    $http.get('/timekeeper/svc/project/list-by-cs-fill?cs=' + $rootScope.user.id).
        success(function(data) {
            $scope.projects = data;
        }).
        error(function(data) {
            $scope.error_msg = data;
        });

    $scope.ok = function () {
        $modalInstance.close();
        $window.location.href = "#/timecard-new/" + $scope.timecard.project.id;
    };

    $scope.cancel = function () {
        $modalInstance.dismiss();
    };
    
});


timecardApp.controller("timecard_view_ctrl", function($log, $rootScope, $scope, $http, $routeParams, $filter, $window) {

    $scope.role=$scope.user.role.shortName;

    $http.get('/timekeeper/svc/timecard/' + $routeParams.tcId).
    success(function(data) {
        $scope.timecard = data
        var start_date = new Date($scope.timecard.project.initialDate);
        var end_date = new Date($scope.timecard.project.endDate);
        
        $scope.days = $filter('dateDiffInDays')(start_date, end_date);
        $scope.weeks = $filter('dateNumOfWeeks')(start_date, end_date);
        
        var tasks = $scope.timecard.project.tasksDTO;
        for (var i = 0; i < tasks.length; i++) {
            var task = tasks[i];
            var tcEntries = [];
            for (var j = 0; j < $scope.timecard.timecardEntriesDTO.length; j++) {
                var tcEntry = $scope.timecard.timecardEntriesDTO[j];
                if (task.id == tcEntry.taskDTO.id) {
                    // datas estao no formato yyyy-mm-dd
                    var y = tcEntry.day.substring(0,4);
                    var m = tcEntry.day.substring(5,7);
                    var d = tcEntry.day.substring(8,10);
                    m = m - 1;
                    tcEntry.day = new Date(y, m, d)
                    tcEntries.push(tcEntry);
                }
            }
            task.tcEntries = tcEntries;
        }
    }).
    error(function(data, status, header, config) {
        $scope.error_msg = data;
    });
    
    $scope.approve = function(timecard) {
        $http.post("/timekeeper/svc/timecard/app-rej/" + timecard.id + "?op=1", timecard.commentPM).
        success(function(data, status, header, config) {
            $scope.saved = true;
            $scope.error_msg = null;
            $window.location.href = "#/timecards/";
        }).
        error(function(data, status, header, config) {
            $scope.error_msg = data;
        });
    };
    
    $scope.reject = function(timecard) {
        $http.post("/timekeeper/svc/timecard/app-rej/" + timecard.id + "?op=2", timecard.commentPM).
        success(function(data, status, header, config) {
            $scope.saved = true;
            $scope.error_msg = null;
            $window.location.href = "#/timecards/";
        }).
        error(function(data, status, header, config) {
            $scope.error_msg = data;
        });
    };
    
    
});
