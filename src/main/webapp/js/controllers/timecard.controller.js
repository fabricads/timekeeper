(function(){
    'use strict';
    /**
     * Timecard's Controllers
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
        
    })

    .controller("timecard_new_ctrl", function($scope, $http, $routeParams, $filter,timecardService) {
        
        $scope.timecard = {};
        $scope.timecard.consultant = {};
        
        timecardService.
            success(function(data) {
                var project = data;
                $scope.timecard.project = data
                var start_date = new Date(project.initialDate);
                var end_date = new Date(project.endDate);
                
                $scope.days = $filter('dateDiffInDays')(start_date, end_date);
                $scope.weeks = $filter('dateNumOfWeeks')(start_date, end_date);

                var tasks = project.tasksDTO;
                for (var i = 0; i < tasks.length; i++) {
                    var task = tasks[i];

                    // set the sunday day of the starting week
                    var initDayWeek = new Date();
                    if ($scope.timecard.project.lastFilledDay == null) {
                        initDayWeek.setDate(start_date.getDate() - start_date.getDay());
                    } else {
                        var sunday = new Date($scope.timecard.project.lastFilledDay);
                        sunday.setDate(sunday.getDate() + 2);
                        initDayWeek = sunday;
                    }
                    if ($scope.timecard.firstDate == null) {
                        $scope.timecard.firstDate =  new Date(initDayWeek.getTime());
                    }
                    
                    var tcEntries = [];
                    for (var j = 0; j < 7; j++) {
                        var tcEntry = {};
                        tcEntry.day = new Date(initDayWeek.getTime());
                        tcEntry.workedHours = 0;
                        tcEntry.workDescription = "";
                        tcEntry.taskDTO = {};
                        tcEntry.taskDTO.id = task.id;
                        if ($scope.timecard.lastDate == null && j == 6) {
                            $scope.timecard.lastDate =  new Date(initDayWeek.getTime());
                        }
                        initDayWeek.setDate(initDayWeek.getDate() + 1);
                        tcEntries.push(tcEntry);
                    }
                    task.tcEntries = tcEntries;
                }
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
        
        $scope.save = function(timecard) {
            timecard.status = 1;
            timecard.timecardEntriesDTO = [];
            while (timecard.project.tasksDTO.length > 0) {
                var task = timecard.project.tasksDTO.shift();
                while (task.tcEntries.length > 0) {
                    var tcEntry = task.tcEntries.shift();
                    timecard.timecardEntriesDTO.push(tcEntry);
                }
            }
            $http.post("/timekeeper/svc/timecard/save", timecard).
                success(function(data, status, header, config) {
                    $scope.saved = true;
                    $scope.error_msg = null;
                }).
                error(function(data, status, header, config) {
                    $scope.error_msg = data;
                });
        };
        
        $scope.submit = function(timecard) {
            timecard.timecardEntriesDTO = [];
            timecard.status = 4;
            while (timecard.project.tasksDTO.length > 0) {
                var task = timecard.project.tasksDTO.shift();
                while (task.tcEntries.length > 0) {
                    var tcEntry = task.tcEntries.shift();
                    timecard.timecardEntriesDTO.push(tcEntry);
                }
            }
            $http.post("/timekeeper/svc/timecard/save", timecard).
            success(function(data, status, header, config) {
                $scope.saved = true;
                $scope.error_msg = null;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
        };
    })
    .controller("tc_new_ctrl", function($scope, $http, $routeParams, $filter,timecardService) {
        
        $scope.timecard = {};
        $scope.timecard.consultant = {};
        
        timecardService.
            success(function(data) {
                var project = data;
                $scope.timecard.project = data
                var start_date = new Date(project.initialDate);
                var end_date = new Date(project.endDate);
                
                $scope.days = $filter('dateDiffInDays')(start_date, end_date);
                $scope.weeks = $filter('dateNumOfWeeks')(start_date, end_date);

                var tasks = project.tasksDTO;
                for (var i = 0; i < tasks.length; i++) {
                    var task = tasks[i];

                    // set the sunday day of the starting week
                    var initDayWeek = new Date();
                    if ($scope.timecard.project.lastFilledDay == null) {
                        initDayWeek.setDate(start_date.getDate() - start_date.getDay());
                    } else {
                        var sunday = new Date($scope.timecard.project.lastFilledDay);
                        sunday.setDate(sunday.getDate() + 2);
                        initDayWeek = sunday;
                    }
                    if ($scope.timecard.firstDate == null) {
                        $scope.timecard.firstDate =  new Date(initDayWeek.getTime());
                    }
                    
                    var tcEntries = [];
                    for (var j = 0; j < 7; j++) {
                        var tcEntry = {};
                        tcEntry.day = new Date(initDayWeek.getTime());
                        tcEntry.workedHours = 0;
                        tcEntry.workDescription = "";
                        tcEntry.taskDTO = {};
                        tcEntry.taskDTO.id = task.id;
                        if ($scope.timecard.lastDate == null && j == 6) {
                            $scope.timecard.lastDate =  new Date(initDayWeek.getTime());
                        }
                        initDayWeek.setDate(initDayWeek.getDate() + 1);
                        tcEntries.push(tcEntry);
                    }
                    task.tcEntries = tcEntries;
                }
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
        
        $scope.save = function(timecard) {
            timecard.status = 1;
            timecard.timecardEntriesDTO = [];
            while (timecard.project.tasksDTO.length > 0) {
                var task = timecard.project.tasksDTO.shift();
                while (task.tcEntries.length > 0) {
                    var tcEntry = task.tcEntries.shift();
                    timecard.timecardEntriesDTO.push(tcEntry);
                }
            }
            $http.post("/timekeeper/svc/timecard/save", timecard).
                success(function(data, status, header, config) {
                    $scope.saved = true;
                    $scope.error_msg = null;
                }).
                error(function(data, status, header, config) {
                    $scope.error_msg = data;
                });
        };
        
        $scope.submit = function(timecard) {
            timecard.timecardEntriesDTO = [];
            timecard.status = 4;
            while (timecard.project.tasksDTO.length > 0) {
                var task = timecard.project.tasksDTO.shift();
                while (task.tcEntries.length > 0) {
                    var tcEntry = task.tcEntries.shift();
                    timecard.timecardEntriesDTO.push(tcEntry);
                }
            }
            $http.post("/timekeeper/svc/timecard/save", timecard).
            success(function(data, status, header, config) {
                $scope.saved = true;
                $scope.error_msg = null;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
        };
    })

    .controller("timecard_cs_list_ctrl", function($rootScope, $scope, $http, $window,$log) {
        $scope.loading = true;
        $http.get('/timekeeper/svc/timecard/list-cs?id=' + $rootScope.user.id).
            success(function(data) {
                $scope.timecards = data;
                $log.debug("Retrieved the following timecards:");
                $log.debug(data);
                $scope.loading = false;
            }).
            error(function(data) {
                $scope.timecards = data;
                $scope.loading = false;
                $scope.error_msg = data;
            });

        $scope.delete = function(tcId) {
            $http.get("/timekeeper/svc/timecard/delete/" + tcId).
                success(function(data, status, header, config) {
                    $scope.saved = true;
                    $scope.error_msg = null;
                    $window.location.reload();
                }).
                error(function(data, status, header, config) {
                    $scope.error_msg = data;
                });
        };
    });
})();