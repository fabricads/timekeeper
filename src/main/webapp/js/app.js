var timekeeperApp = angular
		.module("timekeeperApp", [ "ngRoute", "ngResource", "ui.bootstrap" ]);

timekeeperApp.config([ "$routeProvider", function($routeProvider) {
	
	$routeProvider
	.when("/persons", {
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
	when("/timecard-new/:projectId", {
	    templateUrl : "timecard-new.html",
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
	
	
	when("/organizations", {
		templateUrl : "organizations.html",
	}).
	when("/organization-new", {
		templateUrl : "organization-new.html",
	}).
    when("/organization/:orgId", {
    	templateUrl: "organization-edit.html",
    }).
	
    otherwise({
		redirectTo : "/projects"
	});
	
}

]);

timekeeperApp.controller("TimecardCtrl", function($scope, $http, $routeParams) {

});

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
        if (found == null && $scope.temp_consultant.toSource() != "({})" ) {
			$scope.selected_consultants.push($scope.temp_consultant);
// console.log($scope.selected_consultants);
		}
	};
	
	$scope.remove_consultant = function(consultant) {
		idx = $scope.selected_consultants.indexOf(consultant);
		if (idx > -1) {
// console.log("before: " + $scope.selected_consultants.toSource());
			$scope.selected_consultants.splice(idx, 1);
// console.log("after : " + $scope.selected_consultants.toSource());
		}
	};
	
	$scope.temp_task = {};
	$scope.temp_task.name = "";
	$scope.selected_tasks = [];

    $scope.add_task = function() {
        found = $filter('findByName')($scope.selected_tasks, $scope.temp_task.name);
//        console.log("found task by name ? "); 
//        console.log(found);
        if (!found && $scope.temp_task.name.trim() != "" ) {
            $scope.selected_tasks.push($scope.temp_task);
            $scope.temp_task = {};
            $scope.temp_task.name = "";
//            console.log($scope.selected_tasks);
        }
    };
    
    $scope.remove_task = function(task) {
        idx = $scope.selected_tasks.indexOf(task);
        if (idx > -1) {
//            console.log("before: " + $scope.selected_tasks.toSource());
            $scope.selected_tasks.splice(idx, 1);
//            console.log("after : " + $scope.selected_tasks.toSource());
        }
    };
	
});

timekeeperApp.controller("project_edit_ctrl", function($scope, $http, $routeParams, $rootScope, $filter) {

    
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
        if (found == null && $scope.temp_consultant.toSource() != "({})" ) {
            $scope.selected_consultants.push($scope.temp_consultant);
            console.log($scope.selected_consultants);
        }
    };
    
    $scope.remove_consultant = function(consultant) {
        idx = $scope.selected_consultants.indexOf(consultant);
        if (idx > -1) {
// console.log("before: " + $scope.selected_consultants.toSource());
            $scope.selected_consultants.splice(idx, 1);
// console.log("after : " + $scope.selected_consultants.toSource());
        }
    };

    $scope.temp_task = {};
    $scope.temp_task.name = "";
    $scope.tasks_to_remove = [];

    $scope.add_task = function() {
        found = $filter('findByName')($scope.selected_tasks, $scope.temp_task.name);
//        console.log("found task by name ? "); 
//        console.log(found);
        if (!found && $scope.temp_task.name.trim() != "" ) {
            $scope.selected_tasks.push($scope.temp_task);
            $scope.temp_task = {};
            $scope.temp_task.name = "";
//            console.log($scope.selected_tasks);
        }
    };
    
    $scope.remove_task = function(task) {
        idx = $scope.selected_tasks.indexOf(task);
        if (idx > -1) {
//            console.log("before: " + $scope.selected_tasks.toSource());
            $scope.selected_tasks.splice(idx, 1);
//            console.log("after : " + $scope.selected_tasks.toSource());
            if (task.id != null) {
                $scope.tasks_to_remove.push(task.id);
            }
            console.log("tasks to remove");
            console.log($scope.tasks_to_remove);
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
	$http.get('/timekeeper/svc/organization/list').success(function(data) {
		$scope.orgs = data;
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

timekeeperApp.controller("person_listing_ctrl", function($scope, $http, $window, $routeParams) {
	
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

timekeeperApp.controller("show_modal_select_project", function($scope, $http, $routeParams, $window, $modal) {

    $scope.select_project = function () {
        
        var modalInstance = $modal.open({
          templateUrl: 'modal_select_project.html',
          controller: 'modal_instance'
        });
        
      };
});

timekeeperApp.controller("modal_instance", function($scope, $http, $routeParams, $window, $modalInstance) {

    $scope.timecard = {};
    
    $http.get('/timekeeper/svc/project/list?by-cs=12').
        success(function(data) {
            $scope.projects = data;
        }).
        error(function(data) {
            $scope.error_msg = data;
        });

    $scope.ok = function () {
        $modalInstance.close();
//        console.log($scope.timecard.project.id);
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
    });
    
    
});

timekeeperApp.controller("timecard_new_ctrl", function($scope, $http, $routeParams) {
    
    $scope.timecard = {};
    
    $http.get('/timekeeper/svc/project/'+$routeParams.projectId).
        success(function(data) {
            $scope.project = data;
        }).
        error(function(data, status, header, config) {
            $scope.error_msg = data;
        });
    
    
});

/* ********************************************************
 * 
 * Utilitarios e variaveis globais
 * 
 * ********************************************************
 */

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


timekeeperApp.run(function($rootScope) {
	
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

