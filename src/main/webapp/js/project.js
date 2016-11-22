var projectApp = angular.module("project_ctrl", [ "ngRoute", "ngResource", "ui.bootstrap", "servicesApp", "timekeeperApp" ]);

/*******************************************************************************
 * 
 * Project controllers
 * 
 * ********************************************************
 */

// display all enabled projects
projectApp.controller("project_list_ctrl", function($scope, $http, $window,projectService,$log) {

    // variable to control the printing "loading" while ajax is running
    $scope.loading = true;
    
    // control the parameter e=1, where the query returns only the projects set
    // as enabled.
    $scope.list_enabled = 1;
	
    $scope.refresh = function() {
        projectService.all($scope.list_enabled).
            success(function(data) {
                $log.info(data);
                $scope.projects = data;
                $scope.loading = false;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
                $scope.loading = false;
            });


        /*$http.get('/timekeeper/svc/project/list?e='+$scope.list_enabled).
            success(function(data) {
                $scope.projects = data;
                $scope.loading = false;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
                $scope.loading = false;
            });*/
    };
    
    $scope.refresh();

	
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

// display the enabled projects by consultant
projectApp.controller("project_cs_list_ctrl", function($rootScope, $scope, $http, $window) {
    
    $scope.loading = true;
    $http.get('/timekeeper/svc/project/list-by-cs?cs=' + $rootScope.user.id).
    success(function(data) {
        $scope.projects = data;
        $scope.loading = false;
    });
    
});

projectApp.controller("project_new_ctrl", function($scope, $http, $filter) {
	
	$scope.project = {};
	$scope.project.enabled = true;
	
	// retrieve all project managers
	$http.get('/timekeeper/svc/person/pms').success(function(data) {
        console.log(data);
		$scope.pms = data;
	});
	
	// retrieve all task types
    $http.get('/timekeeper/svc/project/task_types').success(function(data) {
        $scope.taskTypes = data;
    });

    // save a new project
	$scope.project_submit = function(project) {
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

	$scope.temp_task = {};
	$scope.temp_task.name = "";
	$scope.temp_task.dissociateOfProject = true;
    $scope.selected_tasks = [];

    $scope.taskType = {};
    $scope.taskType.id = -1;
    $scope.taskType.name;

    $scope.add_task = function() {
        // control if the selected task is already added
        found = $filter('findByName')($scope.selected_tasks, $scope.temp_task.name);
        if (!found && $scope.temp_task.name.trim() != "" ) {
            // find the task object
            temptaskType = $filter('findById')($scope.taskTypes, $scope.taskType.id);
            if (temptaskType != null) {
                // store only the task type id
                $scope.temp_task.taskType = temptaskType.id;
            }
            $scope.selected_tasks.push($scope.temp_task);
            $scope.temp_task = {};
            $scope.temp_task.dissociateOfProject = true;
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

projectApp.controller("project_edit_ctrl", function($scope, $http, $routeParams, $filter) {

    
    $http.get('/timekeeper/svc/project/'+$routeParams.projectId).
        success(function(data) {
            $scope.project = data;
            $scope.selected_tasks = $scope.project.tasksDTO;
        }).
        error(function(data, status, header, config) {
            $scope.error_msg = data;
        });

    $http.get('/timekeeper/svc/person/pms').success(function(data) {
        $scope.pms = data;
    });
    
    $http.get('/timekeeper/svc/project/task_types').success(function(data) {
        $scope.taskTypes = data;
    });
    
    $scope.project_submit = function(project) {
        project.tasksDTO = $scope.selected_tasks;
        project.tasksToRemove = $scope.tasks_to_remove;
        
        if(project.endDate instanceof Date){
        	project.endDate.setMinutes(project.endDate.getMinutes() - project.endDate.getTimezoneOffset());
        }
        if(project.initialDate instanceof Date){
        	project.initialDate.setMinutes(project.initialDate.getMinutes() - project.initialDate.getTimezoneOffset());
        }
        
        // alert(project.endDate + ' ' + JSON.stringify(project.endDate));
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

    $scope.temp_task = {};
    $scope.temp_task.name = "";
    $scope.temp_task.dissociateOfProject = true;
    $scope.tasks_to_remove = [];
    
    $scope.taskType = {};
    $scope.taskType.id = -1;
    $scope.taskType.name;

    $scope.add_task = function() {
        found = $filter('findByName')($scope.selected_tasks, $scope.temp_task.name);
        if (!found && $scope.temp_task.name.trim() != "" ) {
            temptaskType = $filter('findById')($scope.taskTypes, $scope.taskType.id);
            if (temptaskType != null) {
                $scope.temp_task.taskType = temptaskType.id;
            }
            $scope.selected_tasks.push($scope.temp_task);
            $scope.temp_task = {};
            $scope.temp_task.dissociateOfProject = true;
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

// retrieve a specific task name given a task id
projectApp.filter('taskName', function() {
    return function(input, id) {
        var i=0, len=input.length;
        for (; i<len; i++) {
            if (input[i].id == id) {
                return input[i].name;
            }
        }
        return null;
    }
});
