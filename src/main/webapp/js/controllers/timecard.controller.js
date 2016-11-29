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

    .controller("timecard_new_ctrl", function($log,$scope, $http, $routeParams, $filter,timecardService) {
        
        $scope.timecard = {};
        $scope.timecard.consultant = {};
        $scope.periods={};
        $scope.tasks={}

        /**
         * Gets monday
         */
        function getMonday(){
            var date = new Date();
            date.setDate(date.getDate()-date.getDay());
            return date;
        }

        /**
         * GetsEndDate
         */
        $scope.getEndDate = function(date){
            return (new Date()).setDate(date.getDate()+6);
        }
        /**
         * 2 weeks would be the maximum period... the current one and the one before
         */
        function getPeriods(){
            var periods = [];
            periods.push(getMonday());
            $log.debug(periods[0].getDay());
            periods.push(getMonday());
            periods[0].setDate(periods[0].getDate()-7);
            //$scope.period=periods[1];
            //$log.debug("User has select the date "+$scope.period.getDate());
            return periods;
        }
        $scope.periods = getPeriods();
        $scope.period = 0;

        /**
         * Get Entries according to the start date
         */
        function getEntries(date){
            var project = $scope.timecard.project;
            var tasks = project.tasksDTO;
            for (var i = 0; i < tasks.length; i++) {
                var task = tasks[i];
                var initDayWeek = new Date(date.getTime());
                for (var j = 0; j < task.tcEntries.length; j++) {
                    var tcEntry = task.tcEntries[j];
                    tcEntry.day = new Date(initDayWeek.getTime());
                    if ($scope.timecard.lastDate == null && j == 6) {
                        $scope.timecard.lastDate =  new Date(initDayWeek.getTime());
                    }
                    initDayWeek.setDate(initDayWeek.getDate() + 1);
                }
            }

        }

        /**
         * Changes the period of the entries for each task
         */
        $scope.changePeriod=function(index){
            $log.debug("User has select the date "+$scope.periods[index]);
            if($scope.period!==undefined){
                getEntries($scope.periods[index]);
            }
        };


        $scope.addTask= function(){
            $log.debug("User has added task "+$scope.task.name);
            var project = $scope.timecard.project;
            project.tasksDTO.push($scope.task);
            $scope.task.tcEntries=createEntries($scope.task);
        };

        function createEntries(task){
             // set the sunday day of the starting week
            var initDayWeek = new Date($scope.periods[0].getTime());                  
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
            return tcEntries;
        }
        timecardService.get($routeParams.projectId).
            success(function(data) {
                var project = data;
                $scope.timecard.project = data
                var start_date = new Date(project.initialDate);
                var end_date = new Date(project.endDate);
                
                $scope.days = $filter('dateDiffInDays')(start_date, end_date);
                $scope.weeks = $filter('dateNumOfWeeks')(start_date, end_date);

                $scope.tasks = project.tasksDTO;
                project.tasksDTO=[];
                /*for (var i = 0; i < tasks.length; i++) {
                    var task = tasks[i];

                    // set the sunday day of the starting week
                    var initDayWeek = new Date($scope.periods[0].getTime());                  
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
                }*/
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
    })

    .controller("timecard_edit_ctrl", function($scope, $http, $routeParams, $filter) {
        $http.get('/timekeeper/svc/timecard/' + $routeParams.tcId).
        success(function(data) {
            $scope.timecard = data
            var start_date = new Date($scope.timecard.project.initialDate);
            var end_date = new Date($scope.timecard.project.endDate);
            
            $scope.days = $filter('dateDiffInDays')(start_date, end_date);
            $scope.weeks = $filter('dateNumOfWeeks')(start_date, end_date);
            // 1 = in progress
            // 3 = rejected
            $scope.edit =  $scope.timecard.status == 1 || $scope.timecard.status == 3;
            
            var tasks = $scope.timecard.project.tasksDTO;
            for (var i = 0; i < tasks.length; i++) {
                var task = tasks[i];
    //            console.log("task " + task.name);
                var tcEntries = [];
                for (var j = 0; j < $scope.timecard.timecardEntriesDTO.length; j++) {
                    var tcEntry = $scope.timecard.timecardEntriesDTO[j];
                    if (task.id == tcEntry.taskDTO.id) {
                        // datas estao no formato yyyy-mm-dd
                        var y = tcEntry.day.substring(0,4);
                        var m = tcEntry.day.substring(5,7);
                        var d = tcEntry.day.substring(8,10);
                        m = m - 1;
    //                    console.log("y,m,d " + y + ", " + m + ", " +d);
                        tcEntry.day = new Date(y, m, d)
    //                    console.log(tcEntry);
                        tcEntries.push(tcEntry);
                    }
                }
                task.tcEntries = tcEntries;
            }
        }).
        error(function(data, status, header, config) {
            console.log("Error loading timecard... " + status);
            $scope.error_msg = data;
        });
        
        $scope.save = function(timecard) {
            timecard.timecardEntriesDTO = [];
            //it makes a complete copy
            var tasksDTOBk =  JSON.parse( JSON.stringify(timecard.project.tasksDTO)); 
            while (timecard.project.tasksDTO.length > 0) {
                var task = timecard.project.tasksDTO.shift();
                while (task.tcEntries.length > 0) {
                    var tcEntry = task.tcEntries.shift();
                    timecard.timecardEntriesDTO.push(tcEntry);
                }
            }
            $http.post("/timekeeper/svc/timecard/save", timecard).
            success(function(data, status, header, config) {
                timecard.project.tasksDTO = tasksDTOBk;
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
            //it makes a complete copy
            var tasksDTOBk =  JSON.parse( JSON.stringify(timecard.project.tasksDTO)); 
            while (timecard.project.tasksDTO.length > 0) {
                var task = timecard.project.tasksDTO.shift();
                while (task.tcEntries.length > 0) {
                    var tcEntry = task.tcEntries.shift();
                    timecard.timecardEntriesDTO.push(tcEntry);
                }
            }
            console.log("timecard.status: " + timecard.status);
            $http.post("/timekeeper/svc/timecard/save", timecard).
            success(function(data, status, header, config) {
                timecard.project.tasksDTO = tasksDTOBk;
                $scope.saved = true;
                $scope.error_msg = null;
                $scope.edit = false;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
        };  
    });
})();