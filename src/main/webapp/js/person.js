var personApp = angular.module("person_ctrl", [ "ngRoute", "ngResource", "ui.bootstrap", "servicesApp", "timekeeperApp", "validation.match" ]);

/* ********************************************************
 * 
 * Person controllers
 * 
 * ********************************************************
 */

personApp.controller("person_listing_ctrl", function($filter,$scope, $http, $window,timecardService) {
	
    $scope.list_enabled = 1;
    $scope.loading = true;
    
    $scope.refresh = function() {
    	$http.get('/timekeeper/svc/person/list?e='+$scope.list_enabled).success(function(data) {
    		$scope.persons = data;
    		$scope.loading = false;
    	});
    };
    $scope.refresh();
	
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

	$scope.getTimecard = function(person){
		$scope.buttonTimecardText="Processing";
		$scope.buttonEnable=false;
		timecardService.getAllByPm(person.id).then(function(response){
			var timecards = [];
			var timecardsData = response.data;
			for(var i=0 ; i < timecardsData.length ; i++){
				var oraclepaidId = timecardsData[i].consultant.oraclePAId;
				var name = timecardsData[i].consultant.name;
				var pa=timecardsData[i].project.paNumber;
				for(var j=0 ; j < timecardsData[i].timecardEntriesDTO.length ; j++){
					var task = timecardsData[i].timecardEntriesDTO[j].taskDTO.name;
					var day = timecardsData[i].timecardEntriesDTO[j].day;
					var hours = timecardsData[i].timecardEntriesDTO[j].workedHours;
					var description = timecardsData[i].timecardEntriesDTO[j].workDescription;
					var row = {
						oraclepaidId:oraclepaidId,
						name:name,
						pa:pa,
						task:task,
						day:formatData(day),
						hours:hours,
						description:description || ""
					};
					timecards.push(row);
				}
			}
			console.log(timecards);
			person.timecards=timecards;
			person.getHeader = function () {
				return ["0racle Paid ID", "Name","PA","Task","Data","Worked Hours","Description"]
			};
			person.buttonEnable=true;
		});
	};

	function formatData(data){
		var y = data.substring(0,4);
		var m = data.substring(5,7);
		var d = data.substring(8,10);
		m--;
		return $filter('date')(new Date(y,m,d), 'dd/MM/yyyy');
	}
	
});

personApp.controller("person_new_ctrl", function($scope, $http, $rootScope) {
	
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
	
	$http.get('/timekeeper/svc/organization/list?e=1').success(function(data) {
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

personApp.controller("person_edit_ctrl", function($scope, $http, $routeParams, $rootScope) {
	
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
	
	$http.get('/timekeeper/svc/organization/list?e=1').success(function(data) {
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

personApp.controller("profile_ctrl", function($scope, $http, $routeParams, $rootScope) {
    
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
