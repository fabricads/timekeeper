var projectApp = angular.module("project_ctrl", [ "ngRoute", "ngResource", "ui.utils", "ui.bootstrap", "servicesApp", "timekeeperApp" ]);

/* ********************************************************
 * 
 * Project controllers
 * 
 * ******************************************************** 
 */

projectApp.controller("project_list_ctrl", function($scope, $http, $window) {

    $scope.loading = true;
    $scope.list_enabled = 1;
	
    $scope.refresh = function() {
        $http.get('/timekeeper/svc/project/list?e='+$scope.list_enabled).
            success(function(data) {
                $scope.projects = data;
                $scope.loading = false;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
                $scope.loading = false;
            });
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

projectApp.controller("project_edit_ctrl", function($scope, $http, $routeParams, $filter) {

    
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
