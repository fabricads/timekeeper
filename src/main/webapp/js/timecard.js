var timecardApp = angular.module("timecard_ctrl", [ "ngRoute", "ngResource", "ui.utils", "ui.bootstrap", "servicesApp", "timekeeperApp" ]);

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

timecardApp.controller("timecard_list_ctrl", function($scope, $http, $routeParams) {
    
    $scope.loading = true;
    $http.get('/timekeeper/svc/timecard/list').
        success(function(data) {
            $scope.timecards = data;
            $scope.loading = false;
        }).
        error(function(data) {
            $scope.timecards = data;
            $scope.loading = false;
        });
    
});

timecardApp.controller("timecard_cs_list_ctrl", function($rootScope, $scope, $http, $window) {
    
    $scope.loading = true;
    $http.get('/timekeeper/svc/timecard/list-cs?id=' + $rootScope.user.id).
        success(function(data) {
            $scope.timecards = data;
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

timecardApp.controller("timecard_new_ctrl", function($scope, $http, $routeParams, $filter) {
    
    $scope.timecard = {};
    $scope.timecard.consultant = {};
    
    $http.get('/timekeeper/svc/project/' + $routeParams.projectId + "/tc").
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

    
});

timecardApp.controller("timecard_edit_ctrl", function($scope, $http, $routeParams, $filter) {
    
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

timecardApp.controller("timecard_view_ctrl", function($scope, $http, $routeParams, $filter, $window) {
    
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
