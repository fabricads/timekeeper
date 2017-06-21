(function(){
    'use strict';
    /**
     * Timecard's Controllers
    */
    var timekeeperControllers = angular.module("timekeeperControllers");

    timekeeperControllers.controller("timecard_list_ctrl", function($scope, $log, $http, $routeParams,timecardService) {
        $scope.loading = true;
        
        timecardService.getAllByPm(1).then(
            function(response){
                $log.debug("recebeu timecards ");
                $log.debug(response);
                $scope.timecards=response.data;
                $scope.loading = false;
            },function(error){
                $log.debug("An error has occured "+error.data);
                $scope.timecards = data;
                $scope.loading = false;
            }
        )
        
    })

    timekeeperControllers.controller("modelCtrl",function(id, $log, $scope, $http, $filter) {
        

        $http.get('/timekeeper/svc/timecard/' + id).
        success(function(data) {
            $scope.timecard = data
            var start_date = new Date($scope.timecard.project.initialDate);
            var end_date = new Date($scope.timecard.project.endDate);
            

            var y = $scope.timecard.firstDate.substring(0,4);
            var m = $scope.timecard.firstDate.substring(5,7);
            var d = $scope.timecard.firstDate.substring(8,10);
            m--;
            
            var tc_start_date = new Date(y,m,d);
            y = $scope.timecard.firstDate.substring(0,4);
            m = $scope.timecard.firstDate.substring(5,7);
            d = $scope.timecard.firstDate.substring(8,10);
            m--;

            var tc_last_date = new Date(y,m,d);

            $scope.dates=[];
            for(var i=0;i<7;i++){
                var item = {};
                item.hours=0;
                item.date = new Date(tc_start_date.getFullYear(),tc_start_date.getMonth(),tc_start_date.getDate()+i);
                $scope.dates.push(item);
            }
            
            $scope.days = $filter('dateDiffInDays')(start_date, end_date);
            $scope.weeks = $filter('dateNumOfWeeks')(start_date, end_date);
            $scope.totalHours=0;
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
                        $scope.totalHours+=tcEntry.workedHours;
                        tcEntry.day = new Date(y, m, d)
                        tcEntries.push(tcEntry);
                        $scope.dates[tcEntries.length-1].hours+=tcEntry.workedHours;
                    }
                }
                task.tcEntries = tcEntries;
            }
            console.log($scope.dates);
        }).
        error(function(data, status, header, config) {
            $scope.error_msg = data;
        });
        
    });
    timekeeperControllers.controller("timecard_dashboard_ctrl", function($element, $filter, $scope, 
                                            $uibModal, $log, $http, $routeParams,timecardService,$window) {


        $scope.loading = true;
        $scope.consultants = [];
        timecardService.getPending().then(
            function(response){
                $log.debug("recebeu timecards ");
                $log.debug(response);
                $scope.timecards=response.data;
               
                $scope.consultants = getConsultants($scope.timecards);
                $scope.loading = false;
                $scope.viewTimecards=[];
                for(var i=0; i<$scope.timecards.length; i++){
                    $scope.viewTimecards.push(
                        getTimecards(clone($scope.timecards[i])));
                }
            },function(error){
                $log.debug("An error has occured "+error.data);
                $scope.timecards = data;
                $scope.loading = false;
            }
        )

        function clone(obj) {
            return  JSON.parse(JSON.stringify(obj));
		};

        $scope.getTimecardsByPeriod = function(consultant,periodIndex){
            
            var p = new Date($scope.periods[periodIndex].date.getTime());
            var timecards = clone($scope.timecards);
            var t = [];
            for(var i=0; i<timecards.length; i++){
                if(compareDates(timecards[i].firstDate,p) && consultant.id==timecards[i].consultant.id){
                    t.push(timecards[i]);
                }
            }
            
            $scope.dates=[];

            for(var i=0;i<7;i++){
                var item = {};
                item.hours=0;
                item.date = new Date(p.getFullYear(),p.getMonth(),p.getDate()+i);
                $scope.dates.push(item);
            }

            $scope.viewTimecards=[];
            console.log(t);
            for(var i=0; i<t.length; i++){
                $scope.viewTimecards.push(getTimecards(t[i],p));
            }

            console.log(t);
        };

        function compareDates(dateString,date){
                var y = dateString.substring(0,4);
                var m = dateString.substring(5,7);
                var d = dateString.substring(8,10);
                m = m - 1;
                return (date.getDate()==d && date.getMonth()==m);
        }

        function getTimecards(tc){

            var timecard=clone(tc);
            timecard.totalHours=0;
            var period = getDate(tc.firstDate);
            timecard.dates=[];

            for(var i=0;i<7;i++){
                var item = {};
                item.hours=0;
                item.date = new Date(period.getFullYear(),period.getMonth(),period.getDate()+i);
                timecard.dates.push(item);
            }


            var tasks = timecard.project.tasksDTO;
            for (var i = 0; i < tasks.length; i++) {
                var task = tasks[i];
                var tcEntries = [];
                for (var j = 0; j < timecard.timecardEntriesDTO.length; j++) {
                    var tcEntry = clone(timecard.timecardEntriesDTO[j]);
                    if (task.id == tcEntry.taskDTO.id) {
    
                        tcEntry.day = getDate(tcEntry.day);
                        timecard.totalHours+=tcEntry.workedHours;
                        tcEntries.push(tcEntry);
                        timecard.dates[tcEntries.length-1].hours+=tcEntry.workedHours;
 
                    }
                }
                task.tcEntries = tcEntries;
            }
            //console.log(timecard);
            return timecard;
        }

        $scope.getDetail=function(id){

            var timecard = null ;
            for(var i = 0; i<$scope.timecards.length && timecard==null ; i++){
                if(id==$scope.timecards[i].id){
                    timecard = $scope.timecards[i];
                }
            }
            console.log(timecard);

            if(timecard!= null){
                console.log(id);
                var modalInstance = $uibModal.open({
                    templateUrl: 'model-timecard.html',
                    controller: "modelCtrl",
                    resolve:{
                        id:function(){
                            console.log(timecard.id);
                            return timecard.id;
                        }
                    },
                    size: 'lg'
                });

            }
        };

        $scope.save = function(id){

            timecardService.setOnPA(id).then(function(){
                console.log("saved");
                $window.location.reload();
            },function(){
                console.log("error trying to save the timecard");
            });

        }

        $scope.getPeriods=function(consultant){
            console.log(consultant);
            $scope.selectedPeriod="";
            var timecards = $scope.timecards;
            var periods = [];
            var o=0;
            for(var i=0; i<timecards.length; i++){
                if(timecards[i].consultant.id==consultant.id){
                    var found = false;
                    for(var j=0; j<periods.length && !found; j++){
                        if(compareDates(timecards[i].firstDate,periods[j].date)){
                            found=true;
                        }
                    }
                    if(!found){
                        periods.push({
                            index:o,
                            date:getDate(timecards[i].firstDate)
                        });
                        o++;
                    }
                }
            }
            $scope.periods=periods;
        };

        /**
         * GetsEndDate
         */
        $scope.getEndDate = function(date){
            return (new Date(date.getFullYear(),date.getMonth(),date.getDate()+6));
        };

        function getConsultants(data){
            var result = [];
            for(var i=0; i< data.length; i++){
                result.push(data[i].consultant);
            }
            result=$filter('uniqueID')(result);
            console.log(result);
            return result;
        }
        
        function getDate(date){
                console.log(date);
                var y = date.substring(0,4);
                var m = date.substring(5,7);
                var d = date.substring(8,10);
                m = m - 1;
                return new Date(y, m, d)
        }
    })

    .controller("timecard_new_ctrl2", function($log,$scope, $http, $routeParams, $filter,timecardService, $window) {
        
        $scope.timecard = {};
        $scope.timecard.consultant = {};
        $scope.periods = getPeriods();
        $scope.tasks=[];
        $scope.btnSaveEnbl=false;

        /**
         * Gets monday
         */
        function getSunday(date){
            var date = new Date(date);
            date.setDate(date.getDate()-date.getDay());
            return date;
        }

        /**
         * GetsEndDate
         */
        $scope.getEndDate = function(date){
            return (new Date(date.getFullYear(),date.getMonth(),date.getDate()+6));
        }

        /**
         * 2 weeks would be the maximum period... the current one and the one before
         */
        function getPeriods(){
            
            var periods = [];
            
            $http.get("/timekeeper/svc/timecard/today").then(function(response){

                for(var i = -1, j = 0; j < 3; j++, i++){

                    var monday = getSunday(response.data);
                    var firstDate = new Date(monday.getFullYear(),monday.getMonth(),monday.getDate()-(i*7));
                    var lastDate= new Date(firstDate.getFullYear(),firstDate.getMonth(),firstDate.getDate()+6); 

                    $log.debug("Searching period: "+$filter('date')(firstDate, "yyyy-MM-dd"));
                    $http.get("/timekeeper/svc/timecard/count/"+$routeParams.projectId+"/"+$filter('date')(firstDate, "yyyy-MM-dd")+"/"+$filter('date')(lastDate, "yyyy-MM-dd"))
                    .then(function(response){
                        $log.debug("Recebeu a seguinte response: ");
                        $log.debug(response);
                        if(response.data.count===0){
                            periods.push(new Date(response.data.date));
                        }
                    });

                }

            });

            $log.debug("Periods: ");
            $log.debug(periods);
            return periods;
        }

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
            if(!$scope.btnSaveEnbl){
                $scope.btnSaveEnbl=true;
            }
            $log.debug("User has added task "+$scope.task.name);
            var project = $scope.timecard.project;
            if ($scope.task.id != null ) {
                var found = $filter('findById')(project.tasksDTO, $scope.task.id);
                if (found == null) {
                    project.tasksDTO.push($scope.task);
                    $scope.task.tcEntries=createEntries($scope.task);
                }else{
                    $log.debug("You already selected it!!!");
                }
            }
        };

        function createEntries(task){
             // set the sunday day of the starting week
            var initDayWeek = new Date($scope.periods[$scope.period]);                  
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
                    $window.location.reload();
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
                $window.location.reload();
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
        };
    })
    .controller("timecardPartnerCtrl", function($scope, $log, $http, $routeParams,timecardService) {
        $scope.loading = true;


        timecardService.getAllByPartner().then(
            function(response){
                $log.debug("recebeu timecards ");
                $log.debug(response);
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
        $scope.periods=[];
        $scope.tasks=[];
        $scope.btnSaveEnbl=false;

        /**
         * Gets monday
         */
        function getSunday(){
            var date = new Date();
            date.getDate
            date.setDate(date.getDate()-date.getDay());
            return date;
        }

        /**
         * GetsEndDate
         */
        $scope.getEndDate = function(date){
            return (new Date(date.getFullYear(),date.getMonth(),date.getDate()+6));
        }
        /**
         * 2 weeks would be the maximum period... the current one and the one before
         */
        function getPeriods(){
            var periods = [];
            for(var i = 0;i<2;i++){
                var monday = getSunday();
                var firstDate = new Date(monday.getFullYear(),monday.getMonth(),monday.getDate()-(i*7));
                var lastDate= new Date(firstDate.getFullYear(),firstDate.getMonth(),firstDate.getDate()+6); 
                $log.debug("Searching period: "+$filter('date')(firstDate, "yyyy-MM-dd"));
                $http.get("/timekeeper/svc/timecard/count/"+$routeParams.projectId+"/"+$filter('date')(firstDate, "yyyy-MM-dd")+"/"+$filter('date')(lastDate, "yyyy-MM-dd")).then(function(response){
                    $log.debug("Recebeu a seguinte response: ");
                    $log.debug(response);
                    if(response.data.count===0){
                        periods.push(new Date(response.data.date));
                    }
                });
            }
            $log.debug("Periods: ");
            $log.debug(periods);
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
            if(!$scope.btnSaveEnbl){
                $scope.btnSaveEnbl=true;
            }
            $log.debug("User has added task "+$scope.task.name);
            var project = $scope.timecard.project;
            if ($scope.task.id != null ) {
                var found = $filter('findById')(project.tasksDTO, $scope.task.id);
                if (found == null) {
                    project.tasksDTO.push($scope.task);
                    $scope.task.tcEntries=createEntries($scope.task);
                }else{
                    $log.debug("You already selected it!!!");
                }
            }
        };

        function createEntries(task){
             // set the sunday day of the starting week
            var initDayWeek = new Date($scope.periods[$scope.period]);                  
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
                    $window.location.reload();
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
                $window.location.reload();
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

    .controller("timecard_edit_ctrl", function($scope, $http, $routeParams, $filter,$log,timecardService) {

        $scope.data = undefined;
        $scope.timecard={};

        function getSunday(){
            var date = new Date();
            date.setDate(date.getDate()-date.getDay());
            return date;
        }

        // 1 = in progress
        // 3 = rejected
        function canEdit(){
            var timecard = $scope.timecard;
            var timeWeek = new Date(timecard.firstDate);
            timeWeek.setDate(timeWeek.getFullYear(),timeWeek.getDate()+1);
            $scope.data=timeWeek;
            var minDate = getSunday();
            minDate.setDate(minDate.getDate()-7);
            $log.debug("The firt date of timecard "+timeWeek);
            $log.debug("Min Date "+minDate);
            $log.debug("Is greate or equal "+(timeWeek>=minDate));
            return $scope.timecard.status == 1 || $scope.timecard.status == 3 || (timeWeek>=minDate);
        }

        timecardService.getById($routeParams.tcId).
        success(function(data) {
            $scope.timecard = data;
            $scope.tasks = data.project.tasksDTO;

            var start_date = new Date($scope.timecard.project.initialDate);
            var end_date = new Date($scope.timecard.project.endDate);
            
            $scope.days = $filter('dateDiffInDays')(start_date, end_date);
            $scope.weeks = $filter('dateNumOfWeeks')(start_date, end_date);



            //$scope.edit =  $scope.timecard.status == 1 || $scope.timecard.status == 3;
            $scope.edit =  canEdit();
            
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
            $log.debug("Timecard: ");
            $log.debug($scope.timecard);
        }).
        error(function(data, status, header, config) {
            console.log("Error loading timecard... " + status);
            $scope.error_msg = data;
        });




        $scope.addTask= function(){
            
            var project = $scope.timecard.project;
            if ($scope.task.id != null ) {
                var found = $filter('findById')(project.tasksDTO, $scope.task.id);
                if (found.tcEntries!=undefined && found.tcEntries.length==0) {
                    //project.tasksDTO.push($scope.task);
                    found.tcEntries=createEntries($scope.task);
                }else{
                    $log.debug("You already selected it!!!");
                }
            }
        };

        function createEntries(task){
             // set the sunday day of the starting week


            var y = $scope.timecard.firstDate.substring(0,4);
            var m = $scope.timecard.firstDate.substring(5,7);
            var d = $scope.timecard.firstDate.substring(8,10);
            var initDayWeek = new Date(y,m,d);                  
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
        
        $scope.save = function(timecard) {
            $log.debug("Salvando timecard");
            $log.debug(timecard);
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