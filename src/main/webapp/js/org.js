var orgApp = angular.module("org_ctrl", [ "ngRoute", "ngResource", "ui.bootstrap", "servicesApp", "timekeeperApp" ]);

/* ********************************************************
 * 
 * Organization controllers
 * 
 * ********************************************************
 */

orgApp.controller("organization_new_ctrl", function($scope, $http) {
	console.log("teste");
	$scope.org = {};
	$scope.org.enabled = true;
	$scope.saved = false;
	
	$scope.contact1 = {}
	$scope.contact2 = {}
	
	$scope.org_submit = function(org) {
	    org.contacts = [];
	    if ($scope.contact1 && $scope.contact1.name && $scope.contact1.name.trim() != '') {
	        org.contacts.push($scope.contact1);
	    }
	    if ($scope.contact2 && $scope.contact2.name && $scope.contact2.name.trim() != '') {
	        org.contacts.push($scope.contact2);
	    }
		$http.post("svc/organization/save", org)
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

orgApp.controller("organization_edit_ctrl", function($scope, $http, $routeParams) {
	
    $scope.saved = false;
    $scope.contact1 = {}
    $scope.contact2 = {}
    
	$http.get('svc/organization/'+$routeParams.orgId).
	    success(function(data) {
    		$scope.org = data;
    		if ($scope.org.contacts.length > 0) {
    		    $scope.contact1 = $scope.org.contacts[0]
    		    $scope.contact2 = $scope.org.contacts[1]
    		} 
    	}).
    	error(function(data, status, header, config) {
    	    $scope.error_msg = data;
        });

	
	$scope.org_submit = function(org) {
	    org.contacts = [];
	    if ($scope.contact1 && $scope.contact1.name && $scope.contact1.name.trim() != '') {
            org.contacts.push($scope.contact1);
        }
        if ($scope.contact2 && $scope.contact2.name && $scope.contact2.name.trim() != '') {
            org.contacts.push($scope.contact2);
        }
		$http.post("svc/organization/save", org).
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

orgApp.controller("org_listing_ctrl", function($scope, $http, $routeParams, $window) {
	
    $scope.list_enabled = 1;
    $scope.loading = true;
    
	$scope.refresh = function() {
	    $http.get('svc/organization/list?e='+$scope.list_enabled).
    	    success(function(data) {
        		$scope.orgs = data;
        		$scope.loading = false;
        	}).
        	error(function(data, status, header, config) {
                $scope.error_msg = data;
                $scope.loading = false;
            });
	};
	
	$scope.refresh();
	
	$scope.disable = function(orgId) {
	    $http.get("svc/organization/"+orgId+"/disable");
	    $window.location.reload();
	};
	$scope.enable = function(orgId) {
		$http.get("svc/organization/"+orgId+"/enable");
		$window.location.reload();
	};
	$scope.delete = function(orgId) {
		$http.get("svc/organization/"+orgId+"/delete");
		$window.location.reload();
	};
	
});

