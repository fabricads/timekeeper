var timekeeperApp = angular
		.module("timekeeperApp", [ "ngRoute", "ngResource", "ui.bootstrap" ]);

timekeeperApp.config([ "$routeProvider", function($routeProvider) {
	
	$routeProvider
	.when("/persons", {
		templateUrl : "persons.html",
	}).
	when("/person-new", {
	    templateUrl : "person.html",
	    controller: "person_new_ctrl"
	}).
	when("/person/:personId", {
	    templateUrl: "person.html",
	    controller: "person_edit_ctrl"
	}).
	when("/profile/:personId", {
	    templateUrl: "profile.html",
	    controller: "profile_ctrl"
	}).
	
	when("/timecards", {
		templateUrl : "timecards.html",
	}).
	
	when("/projects", {
		templateUrl : "projects.html",
	}).
	when("/project-new", {
	    templateUrl : "project.html",
	    controller: "project_new_ctrl"
	}).
	when("/project/:projectId", {
	    templateUrl: "project.html",
	    controller: "project_edit_ctrl"
	}).
	
	
	when("/organizations", {
		templateUrl : "organizations.html",
	}).
	when("/organization-new", {
		templateUrl : "organization.html",
		controller: "organization_new_ctrl"
	}).
    when("/organization/:orgId", {
    	templateUrl: "organization.html",
    	controller: "organization_edit_ctrl"
    }).
	
    otherwise({
		redirectTo : "/projects"
	});
	
}

]);

timekeeperApp.controller("TimecardCtrl", function($scope, $http, $routeParams) {

});

/*
 * Project controllers
 * 
 */

timekeeperApp.controller("project_list_ctrl", function($scope, $http) {

	$http.get('/timekeeper/svc/project/list').success(function(data) {
		$scope.projects = data;
	});

});

timekeeperApp.controller("project_new_ctrl", function($scope, $http) {
	
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
		$http.post("/timekeeper/svc/project/save", project).success(
				function(data, status, header, config) {
				}).error(function(data, status, header, config) {
					alert("Error to save project: " + status);
					
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

	$scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
	$scope.format = $scope.formats[0];
	
	$scope.selected_consultants = [];
	$scope.temp_consultant = {};
	
	$scope.add_consultant = function() {
		if ($scope.selected_consultants.indexOf($scope.temp_consultant) < 0 && $scope.temp_consultant.toSource() != "({})" ) {
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
	
});

timekeeperApp.controller("project_edit_ctrl", function($scope, $http, $routeParams, $rootScope) {

    
    $http.get('/timekeeper/svc/project/'+$routeParams.projectId).success(function(data) {
        $scope.project = data;
    });

    $http.get('/timekeeper/svc/person/pms').success(function(data) {
        $scope.pms = data;
    });
    
    $http.get('/timekeeper/svc/person/consultants').success(function(data) {
        $scope.consultants = data;
    });
    
    $scope.project_submit = function(project) {
        project.consultants = $scope.selected_consultants;
        $http.post("/timekeeper/svc/project/save", project).success(
            function(data, status, header, config) {
            }).error(function(data, status, header, config) {
                alert("Error to save project: " + status);
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

    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    $scope.format = $scope.formats[0];
    
    $scope.selected_consultants = [];
    $scope.temp_consultant = {};
    
    $scope.add_consultant = function() {
        if ($scope.selected_consultants.indexOf($scope.temp_consultant) < 0 && $scope.temp_consultant.toSource() != "({})" ) {
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

    
});



/*
 * Organization controllers
 * 
 */

timekeeperApp.controller("organization_new_ctrl", function($scope, $http) {
	
	$scope.org = {};
	$scope.org.enabled = true;
	
	$scope.org_submit = function(org) {
		$http.post("/timekeeper/svc/organization/save", org)
			.success(
				function(data, status, header, config) {
					
				}
			)
			.error(
				function(data, status, header, config) {
					alert("Error to submit new organization: " + status);

				}
			);
	};

});

timekeeperApp.controller("organization_edit_ctrl", function($scope, $http, $routeParams) {
	
	$http.get('/timekeeper/svc/organization/'+$routeParams.orgId).success(function(data) {
		$scope.org = data;
	});

	
	$scope.org_submit = function(org) {
		$http.post("/timekeeper/svc/organization/save", org).success(
				function(data, status, header, config) {
					
				}).error(function(data, status, header, config) {
					alert("Error to submit new organization: " + status);
				});
	};
	
});

timekeeperApp.controller("org_listing_ctrl", function($scope, $http, $route, $routeParams) {
	
	$http.get('/timekeeper/svc/organization/list').success(function(data) {
		$scope.orgs = data;
	});
	
	$scope.disable = function(orgId) {
		$http.get("/timekeeper/svc/organization/"+orgId+"/disable");
		$route.reload();
	};
	$scope.enable = function(orgId) {
		$http.get("/timekeeper/svc/organization/"+orgId+"/enable");
		$route.reload();
	};
	$scope.delete = function(orgId) {
		$http.get("/timekeeper/svc/organization/"+orgId+"/delete");
		$route.reload();
	};
	
});

/*
 * Person controllers
 * 
 */

timekeeperApp.controller("person_listing_ctrl", function($scope, $http, $route, $routeParams) {
	
	$http.get('/timekeeper/svc/person/list').success(function(data) {
		$scope.persons = data;
	});
	
	$scope.disable = function(personId) {
		$http.get("/timekeeper/svc/person/"+personId+"/disable");
		$route.reload();
	};
	$scope.enable = function(personId) {
		$http.get("/timekeeper/svc/person/"+personId+"/enable");
		$route.reload();
	};
	$scope.delete = function(personId) {
		$http.get("/timekeeper/svc/person/"+personId+"/delete");
		$route.reload();
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
		$http.post("/timekeeper/svc/person/save", person)
		.success(
				function(data, status, header, config) {
					
				}
		)
		.error(
				function(data, status, header, config) {
					alert("Error to save organization: " + status);
					
				}
		);
	};
	
});

timekeeperApp.controller("person_edit_ctrl", function($scope, $http, $routeParams, $rootScope) {
	
	$http.get('/timekeeper/svc/person/'+$routeParams.personId).success(function(data) {
		$scope.person = data;
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
		.success(
				function(data, status, header, config) {
				}
		)
		.error(
				function(data, status, header, config) {
					alert("Error to save organization: " + status);
					
				}
		);
	};
	
});

timekeeperApp.controller("profile_ctrl", function($scope, $http, $routeParams, $rootScope) {
    
    $http.get('/timekeeper/svc/person/'+$routeParams.personId).success(function(data) {
        $scope.person = data;
    });
    
    $scope.states = $rootScope.states;	
    
    $scope.person_submit = function(person) {
        $http.post("/timekeeper/svc/person/save", person)
        .success(
                function(data, status, header, config) {
                }
        )
        .error(
                function(data, status, header, config) {
                    alert("Error to save person: " + status);
                    
                }
        );
    };
    
});


/*
 * Person controllers
 * 
 */

timekeeperApp.controller("timecard_list_ctrl", function($scope, $http, $route, $routeParams) {
    
    $http.get('/timekeeper/svc/timecard/list').success(function(data) {
        $scope.timecards = data;
    });
    
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

