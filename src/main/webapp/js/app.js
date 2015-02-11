var timekeeperApp = angular.module("timekeeperApp", [ "ngRoute", "ngResource", "ui.bootstrap", "servicesApp" ]);

timekeeperApp.config([ "$routeProvider", function($routeProvider) {
	$routeProvider.
	
	when("/persons", {
	    templateUrl : "persons.html",
	}).
	when("/person-new", {
	    templateUrl : "person-new.html",
	}).
	when("/person/:personId", {
	    templateUrl: "person-edit.html",
	}).
	when("/profile/:personId", {
	    templateUrl: "profile.html",
	}).
	
	when("/timecards", {
		templateUrl : "timecards.html",
	}).
	when("/timecards-cs", {
	    templateUrl : "timecards-cs.html",
	}).
	when("/timecard-new/:projectId", {
	    templateUrl : "timecard-new.html",
	}).
	when("/timecard-edit/:tcId", {
	    templateUrl : "timecard-edit.html",
	}).
	
	when("/projects", {
		templateUrl : "projects.html",
	}).
	when("/project-new", {
	    templateUrl : "project-new.html",
	}).
	when("/project/:projectId", {
	    templateUrl: "project-edit.html",
	}).
	
    when("/projects-cs", {
        templateUrl : "projects-cs.html",
    }).
	
	
	when("/organizations", {
		templateUrl : "organizations.html",
	}).
	when("/organization-new", {
		templateUrl : "organization-new.html",
	}).
    when("/organization/:orgId", {
    	templateUrl: "organization-edit.html",
    }).
    
    when("/organization/:orgId", {
        templateUrl: "organization-edit.html",
    });
	
}]);

/* ********************************************************
 * 
 * Project controllers
 * 
 * ******************************************************** 
 */

timekeeperApp.controller("project_list_ctrl", function($scope, $http, $window) {

    $scope.loading = true;
	$http.get('/timekeeper/svc/project/list').
	    success(function(data) {
        	$scope.projects = data;
        	$scope.loading = false;
        });
	
	$scope.disable = function(projectId) {
        $http.get("/timekeeper/svc/project/"+projectId+"/disable");
        $window.location.reload();
    };
    $scope.enable = function(projectId) {
        $http.get("/timekeeper/svc/project/"+projectId+"/enable");
        $window.location.reload();
    };
    $scope.delete = function(projectId) {
        $http.get("/timekeeper/svc/project/"+projectId+"/delete");
        $window.location.reload();
    };

});

timekeeperApp.controller("project_cs_list_ctrl", function($rootScope, $scope, $http, $window) {
    
    $scope.loading = true;
    $http.get('/timekeeper/svc/project/list-by-cs?cs=' + $rootScope.user.id).
    success(function(data) {
        $scope.projects = data;
        $scope.loading = false;
    });
    
});

timekeeperApp.controller("project_new_ctrl", function($scope, $http, $filter) {
	
	$scope.project = {};
	$scope.project.enabled = true;
	
	$http.get('/timekeeper/svc/person/pms').success(function(data) {
		$scope.pms = data;
	});
	
	$http.get('/timekeeper/svc/person/consultants').success(function(data) {
		$scope.consultants = data;
	});

	$scope.project_submit = function(project) {
		project.consultants = $scope.selected_consultants;
		project.tasksDTO = $scope.selected_tasks;
		$http.post("/timekeeper/svc/project/save", project).success(
				function(data, status, header, config) {
				    $scope.saved = true;
	                $scope.error_msg = null;
	                $scope.prj_name = project.name;
	            }).
	            error(function(data, status, header, config) {
	                $scope.error_msg = data;
	            });
	};
    
	$scope.open = function($event) {
	    $event.preventDefault();
	    $event.stopPropagation();
	    $scope.opened = true;
	};
	$scope.open2 = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.opened2 = true;
	};

	$scope.dateOptions = {
		"show-weeks": false	
	};

	$scope.selected_consultants = [];
	$scope.temp_consultant = {};
	
	$scope.add_consultant = function() {
	    found = $filter('findById')($scope.selected_consultants, $scope.temp_consultant.id);
        if (found == null && $scope.temp_consultant.id != null ) {
			$scope.selected_consultants.push($scope.temp_consultant);
		}
	};
	
	$scope.remove_consultant = function(consultant) {
		idx = $scope.selected_consultants.indexOf(consultant);
		if (idx > -1) {
			$scope.selected_consultants.splice(idx, 1);
		}
	};
	
	$scope.temp_task = {};
	$scope.temp_task.name = "";
	$scope.selected_tasks = [];

    $scope.add_task = function() {
        found = $filter('findByName')($scope.selected_tasks, $scope.temp_task.name);
        if (!found && $scope.temp_task.name.trim() != "" ) {
            $scope.selected_tasks.push($scope.temp_task);
            $scope.temp_task = {};
            $scope.temp_task.name = "";
        }
    };
    
    $scope.remove_task = function(task) {
        idx = $scope.selected_tasks.indexOf(task);
        if (idx > -1) {
            $scope.selected_tasks.splice(idx, 1);
        }
    };
	
});

timekeeperApp.controller("project_edit_ctrl", function($scope, $http, $routeParams, $filter) {

    
    $http.get('/timekeeper/svc/project/'+$routeParams.projectId).
        success(function(data) {
            $scope.project = data;
            $scope.selected_consultants = $scope.project.consultants;
            $scope.selected_tasks = $scope.project.tasksDTO;
        }).
        error(function(data, status, header, config) {
            $scope.error_msg = data;
        });

    $http.get('/timekeeper/svc/person/pms').success(function(data) {
        $scope.pms = data;
    });
    
    $http.get('/timekeeper/svc/person/consultants').success(function(data) {
        $scope.consultants = data;
    });
    
    $scope.project_submit = function(project) {
        project.consultants = $scope.selected_consultants;
        project.tasksDTO = $scope.selected_tasks;
        project.tasksToRemove = $scope.tasks_to_remove;
        $http.post("/timekeeper/svc/project/save", project).success(
                function(data, status, header, config) {
                    $scope.saved = true;
                    $scope.error_msg = null;
                    $scope.prj_name = project.name;
                }).
                error(function(data, status, header, config) {
                    $scope.error_msg = data;
                });
    };
    
    $scope.open = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.opened = true;
    };
    $scope.open2 = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.opened2 = true;
    };

    $scope.dateOptions = {
        "show-weeks": false 
    };

    $scope.temp_consultant = {};
    
    $scope.add_consultant = function() {
        found = $filter('findById')($scope.selected_consultants, $scope.temp_consultant.id);
        if (found == null && $scope.temp_consultant.id != null ) {
            $scope.selected_consultants.push($scope.temp_consultant);
        }
    };
    
    $scope.remove_consultant = function(consultant) {
        idx = $scope.selected_consultants.indexOf(consultant);
        if (idx > -1) {
            $scope.selected_consultants.splice(idx, 1);
        }
    };

    $scope.temp_task = {};
    $scope.temp_task.name = "";
    $scope.tasks_to_remove = [];

    $scope.add_task = function() {
        found = $filter('findByName')($scope.selected_tasks, $scope.temp_task.name);
        if (!found && $scope.temp_task.name.trim() != "" ) {
            $scope.selected_tasks.push($scope.temp_task);
            $scope.temp_task = {};
            $scope.temp_task.name = "";
        }
    };
    
    $scope.remove_task = function(task) {
        idx = $scope.selected_tasks.indexOf(task);
        if (idx > -1) {
            $scope.selected_tasks.splice(idx, 1);
            if (task.id != null) {
                $scope.tasks_to_remove.push(task.id);
            }
        }
    };
    
});



/* ********************************************************
 * 
 * Organization controllers
 * 
 * ********************************************************
 */

timekeeperApp.controller("organization_new_ctrl", function($scope, $http) {
	
	$scope.org = {};
	$scope.org.enabled = true;
	$scope.saved = false;
	
	$scope.org_submit = function(org) {
//	    org.name = org.name.trim();
		$http.post("/timekeeper/svc/organization/save", org)
			.success(function(data, status, header, config) {
				$scope.saved = true;
				$scope.error_msg = null;
				$scope.org_name = org.name;
			}).
			error(function(data, status, header, config) {
			    $scope.error_msg = data;
			});
	};

});

timekeeperApp.controller("organization_edit_ctrl", function($scope, $http, $routeParams) {
	
    $scope.saved = false;
    
	$http.get('/timekeeper/svc/organization/'+$routeParams.orgId).
	    success(function(data) {
    		$scope.org = data;
    	}).
    	error(function(data, status, header, config) {
    	    $scope.error_msg = data;
        });

	
	$scope.org_submit = function(org) {
//	    org.name = org.name.trim();
		$http.post("/timekeeper/svc/organization/save", org).
	        success(function(data, status, header, config) {
	            $scope.saved = true;
	            $scope.error_msg = null;
	            $scope.org_name = org.name;
			}).
			error(function(data, status, header, config) {
			    $scope.error_msg = data;
			});
	};
	
});

timekeeperApp.controller("org_listing_ctrl", function($scope, $http, $routeParams, $window) {
	
    $scope.loading = true;
	$http.get('/timekeeper/svc/organization/list').
	    success(function(data) {
    		$scope.orgs = data;
    		$scope.loading = false;
    	}).
    	error(function(data, status, header, config) {
            $scope.error_msg = data;
            $scope.loading = false;
        });
	
	$scope.disable = function(orgId) {
		$http.get("/timekeeper/svc/organization/"+orgId+"/disable");
		$window.location.reload();
	};
	$scope.enable = function(orgId) {
		$http.get("/timekeeper/svc/organization/"+orgId+"/enable");
		$window.location.reload();
	};
	$scope.delete = function(orgId) {
		$http.get("/timekeeper/svc/organization/"+orgId+"/delete");
		$window.location.reload();
	};
	
});

/* ********************************************************
 * 
 * Person controllers
 * 
 * ********************************************************
 */

timekeeperApp.controller("person_listing_ctrl", function($scope, $http, $window) {
	
    $scope.loading = true;
	$http.get('/timekeeper/svc/person/list').success(function(data) {
		$scope.persons = data;
		$scope.loading = false;
	});
	
	$scope.disable = function(personId) {
		$http.get("/timekeeper/svc/person/"+personId+"/disable");
		$window.location.reload();
	};
	$scope.enable = function(personId) {
		$http.get("/timekeeper/svc/person/"+personId+"/enable");
		$window.location.reload();
	};
	$scope.delete = function(personId) {
		$http.get("/timekeeper/svc/person/"+personId+"/delete");
		$window.location.reload();
	};
	
});

timekeeperApp.controller("person_new_ctrl", function($scope, $http, $rootScope) {
	
	$scope.person = {};
	$scope.person.enabled = true;
	$scope.person.country = "Brazil";
	
	$scope.password_confirmation;
	
	$scope.states = $rootScope.states;	
	
	$http.get('/timekeeper/svc/person/types').success(function(data) {
		$scope.personTypes = data;
	});
	$http.get('/timekeeper/svc/role/list').success(function(data) {
		$scope.roles = data;
	});
	
	$http.get('/timekeeper/svc/organization/list?e=true').success(function(data) {
		$scope.orgs = data;
	});
	
	$scope.person_submit = function(person) {
		$http.post("/timekeeper/svc/person/save", person).
		    success(function(data, status, header, config) {
			    $scope.saved = true;
                $scope.error_msg = null;
                $scope.person_name = person.name;
			}).
			error(function(data, status, header, config) {
			    $scope.error_msg = data;
			});
	};
	
});

timekeeperApp.controller("person_edit_ctrl", function($scope, $http, $routeParams, $rootScope) {
	
	$http.get('/timekeeper/svc/person/'+$routeParams.personId).
	    success(function(data) {
    		$scope.person = data;
    	}).
    	error(function(data, status, header, config) {
            $scope.error_msg = data;
        });
	
	$scope.password_confirmation = null;
	$scope.states = $rootScope.states;	
	
	$http.get('/timekeeper/svc/person/types').success(function(data) {
		$scope.personTypes = data;
	});
	$http.get('/timekeeper/svc/role/list').success(function(data) {
		$scope.roles = data;
	});
	
	$http.get('/timekeeper/svc/organization/list?e=true').success(function(data) {
		$scope.orgs = data;
	});
	
	$scope.person_submit = function(person) {
		$http.post("/timekeeper/svc/person/save", person)
		    .success(function(data, status, header, config) {
		        $scope.saved = true;
                $scope.error_msg = null;
                $scope.person_name = person.name;
			}).
			error(function(data, status, header, config) {
			    $scope.error_msg = data;
					
			});
	};
	
});

timekeeperApp.controller("profile_ctrl", function($scope, $http, $routeParams, $rootScope) {
    
    $http.get('/timekeeper/svc/profile/'+$routeParams.personId).success(function(data) {
        $scope.person = data;
    });
    
    $scope.password_confirmation = null;
    $scope.states = $rootScope.states;	
    
    $scope.person_submit = function(person) {
        $http.post("/timekeeper/svc/profile/save", person).
            success(function(data, status, header, config) {
                $scope.saved = true;
                $scope.error_msg = null;
                $scope.person_name = person.name;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
    };
    
});


/* ********************************************************
 * 
 * Timecard controllers
 * 
 * ********************************************************
 */

timekeeperApp.controller("show_modal_select_project", function($scope, $modal) {

    $scope.select_project = function () {
        
        var modalInstance = $modal.open({
          templateUrl: 'modal_select_project.html',
          controller: 'modal_instance'
        });
        
      };
});

timekeeperApp.controller("modal_instance", function($rootScope, $scope, $http, $window, $modalInstance) {

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

timekeeperApp.controller("timecard_list_ctrl", function($scope, $http, $routeParams) {
    
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

timekeeperApp.controller("timecard_cs_list_ctrl", function($rootScope, $scope, $http, $routeParams) {
    
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
    
});

timekeeperApp.controller("timecard_new_ctrl", function($scope, $http, $routeParams, $filter) {
    
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
                    tcEntry.workedHours = "";
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

timekeeperApp.controller("timecard_edit_ctrl", function($scope, $http, $routeParams, $filter) {
    
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
        $scope.error_msg = data;
    });
    
    $scope.save = function(timecard) {
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
        console.log("timecard.status: " + timecard.status);
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


/* ********************************************************
 * 
 * Utilitarios e variaveis globais
 * 
 * ********************************************************
 */

timekeeperApp.controller("menu_ctrl", function(MessageService, $scope, $rootScope, $window, $http) {
    if ($rootScope.user != null) {
        $scope.user = $rootScope.user;
    }
    
    $scope.role = function(roles) {
        for (var i = 0; i < roles.length; i++) {
            if (roles[i] == $scope.user.role.shortName) {
                return true;
            }
        }
    };
    
    $scope.logout = function() {
        $http.get("/timekeeper/svc/auth/logout").
        success(function(data, status, header, config) {
            sessionStorage.removeItem("user");
            $window.location.href = "login.html";
        });
    };
    
});

timekeeperApp.controller("message_ctrl", function(MessageService, $scope) {
    $scope.hasMessages = function() {
        return MessageService.hasMessages();
    };
    
    $scope.clearMessages = function() {
        MessageService.clearMessages();
    };
    
});


timekeeperApp.directive('float', function() {
	return {
		require : 'ngModel',
		link : function(scope, ele, attr, ctrl) {
			ctrl.$parsers.unshift(function(viewValue) {
				return parseFloat(viewValue, 10);
			});
		}
	};
});

// from https://github.com/TheSharpieOne/angular-input-match/blob/master/dist/angular-input-match.js
// used to test password confirmation on person-edit.html person-new.html
timekeeperApp.directive('match', function($parse) {
    return {
        require: '?ngModel',
        restrict: 'A',
        link: function(scope, elem, attrs, ctrl) {
            if(!ctrl) {
                if(console && console.warn){
                    console.warn('Match validation requires ngModel to be on the element');
                }
                return;
            }

            var matchGetter = $parse(attrs.match);

            scope.$watch(getMatchValue, function(){
                ctrl.$validate();
            });

            ctrl.$validators.match = function(){
                return ctrl.$viewValue === getMatchValue();
            };

            function getMatchValue(){
                var match = matchGetter(scope);
                if(angular.isObject(match) && match.hasOwnProperty('$viewValue')){
                    match = match.$viewValue;
                }
                return match;
            }
        }
    };
});

timekeeperApp.filter('findById', function() {
    return function(input, id) {
      var i=0, len=input.length;
      for (; i<len; i++) {
        if (+input[i].id == +id) {
          return input[i];
        }
      }
      return null;
    }
});

timekeeperApp.filter('findByName', function() {
    return function(input, name) {
        var i=0, len=input.length;
        for (; i<len; i++) {
            if (input[i].name == name) {
                return input[i];
            }
        }
        return null;
    }
});

timekeeperApp.filter('dateDiffInDays', function () {
    var magicNumber = (1000 * 60 * 60 * 24);

    return function (fromDate, toDate) {
      if(toDate && fromDate){
        var num = (toDate - fromDate) / magicNumber;
        var dayDiff = Math.ceil(num);
        return dayDiff;
      }
    };
});

timekeeperApp.filter('dateNumOfWeeks', function () {
    return function (fromDate, toDate) {
        if(toDate && fromDate){
            var week1 = fromDate.getWeek();
            var week2 = toDate.getWeek();
            var weekDiff = week2 - week1 + 1;
            return weekDiff;
        }
    };
});

// from http://weeknumber.net/how-to/javascript
// used on timecard pages, to calculate the number of weeks to present to the user.
Date.prototype.getWeek = function() { 
    var date = new Date(this.getTime()); 
    date.setHours(0, 0, 0, 0); 
    // Thursday in current week decides the year. 
    date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7); 
    // January 4 is always in week 1. 
    var week1 = new Date(date.getFullYear(), 0, 4); 
    // Adjust to Thursday in week 1 and count number of weeks from date to week1. 
    return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000 - 3 + (week1.getDay() + 6) % 7) / 7); 
}


timekeeperApp.factory('authHttpResponseInterceptor',['$q','$location', '$window', 'MessageService', function($q, $location, $window, MessageService){
    return {
        response: function(response){
            if (response.status === 401) {
                console.log("Response 401");
            } else if (response.status === 403) {
                console.log("Response 403");
            }
            return response || $q.when(response);
        },
        responseError: function(rejection) {
            if (rejection.status === 401) {
                console.log("Response Error 401",rejection);
                $window.location.href = "login.html";
            } else if (rejection.status === 400) {
                console.log("Response Error 400",rejection);
            } else if (rejection.status === 403) {
                MessageService.setMessages(rejection.data.message);
                console.log("Response Error 403", rejection);
            }
            return $q.reject(rejection);
        }
    }
}]);

timekeeperApp.config(['$httpProvider',function($httpProvider) {
    //Http Intercpetor to check auth failures for xhr requests
    $httpProvider.interceptors.push('authHttpResponseInterceptor');
}]);

timekeeperApp.run(function($rootScope, $window, MessageService) {

    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
        MessageService.clearMessages();
    });
    
    $rootScope.user = JSON.parse(sessionStorage.getItem("user"));
    var user2 = JSON.parse(sessionStorage.getItem("user"));
    
	$rootScope.states= [{
		"id": "AC",
		"name": "Acre"
	},
	     {
		"id": "AL",
		"name": "Alagoas"
	},
	     {
		"id": "AM",
		"name": "Amazonas"
	},
	     {
		"id": "AP",
		"name": "Amapá"
	},
	     {
		"id": "BA",
		"name": "Bahia"
	},
	     {
		"id": "CE",
		"name": "Ceará"
	},
	     {
		"id": "DF",
		"name": "Distrito Federal"
	},
	     {
		"id": "ES",
		"name": "Espírito Santo"
	},
	     {
		"id": "GO",
		"name": "Goiás"
	},
	     {
		"id": "MA",
		"name": "Maranhão"
	},
	     {
		"id": "MG",
		"name": "Minas Gerais"
	},
	     {
		"id": "MS",
		"name": "Mato Grosso do Sul"
	},
	     {
		"id": "MT",
		"name": "Mato Grosso"
	},
	     {
		"id": "PA",
		"name": "Pará"
	},
	     {
		"id": "PB",
		"name": "Paraíba"
	},
	     {
		"id": "PE",
		"name": "Pernambuco"
	},
	     {
		"id": "PI",
		"name": "Piauí"
	},
	     {
		"id": "PR",
		"name": "Paraná"
	},
	     {
		"id": "RJ",
		"name": "Rio de Janeiro"
	},
	     {
		"id": "RN",
		"name": "Rio Grande do Norte"
	},
	     {
		"id": "RO",
		"name": "Rondônia"
	},
	     {
		"id": "RR",
		"name": "Roraima"
	},
	     {
		"id": "RS",
		"name": "Rio Grande do Sul"
	},
	     {
		"id": "SC",
		"name": "Santa Catarina"
	},
	     {
		"id": "SE",
		"name": "Sergipe"
	},
	     {
		"id": "SP",
		"name": "São Paulo"
	},
	     {
		"id": "TO",
		"name": "Tocantins"
	}];
});

