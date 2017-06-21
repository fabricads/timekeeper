var loginApp = angular.module("loginApp", [ "ngRoute", "ngResource", "ui.bootstrap", "servicesApp", "validation.match"]);

loginApp.config([ "$routeProvider", function($routeProvider) {
	
	$routeProvider.
	when("/login", {
	    templateUrl : "login.html",
	}).
	when("/reset-password/:hash", {
	    templateUrl : "reset-password.html",
	}).
	
    otherwise({
		redirectTo : "/login"
	});
}

]);

loginApp.controller("login_ctrl", function($scope, $rootScope, $location, $window, AUTH_EVENTS, auth_service, $uibModal) {
    
    $scope.error_msg = $rootScope.error_msg;
    $scope.login = function(person) {
        auth_service.login(person, function(data, status) {
            if (status == 200) {
                if (data.email != null) { 
                    sessionStorage.setItem("user", JSON.stringify(data));
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                    $window.location.href = ".";
                } else {
                    console.log("already logged in");
                    $window.location.href = ".";
                }
            } else {
                console.log(">> login failed");
                $scope.error_msg = "E-mail or password incorrect.";
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            }
        });
    };
    
    $scope.forget_password = function () {
        var modalInstance = $uibModal.open({
          templateUrl: 'modal_forget_password.html',
          controller: 'modal_instance'
        });
      };

});

loginApp.controller("reset_password_ctrl", function($scope, $rootScope, $location, $window, $routeParams, $http) {
    
    console.log("url reset-password ctrl");
    $scope.password_confirmation = null;
    
    $http.get('svc/auth/check/' + $routeParams.hash).
        success(function(data) {
            $scope.person = data;
        }).
        error(function(data, status, header, config) {
            $scope.error_msg = data;
        });

    
    $scope.reset = function(person) {
        person.hash = $routeParams.hash;
        $http.post("svc/auth/reset", person)
            .success(function(data, status, header, config) {
                $scope.resetOk = true;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
                    
            });
    };
    
});

loginApp.controller("modal_instance", function($rootScope, $scope, $http, $window, $uibModalInstance) {

    $scope.send = function () {
        $http.get("svc/auth/forgot/" + $scope.email).success(
            function(data, status, header, config) {
                $scope.msg = "Check your e-mail";
                $uibModalInstance.close();
            }).
            error(function(data, status, header, config) {
                if (status == 404) {
                    $scope.error_msg = {};
                    $scope.error_msg.error = "E-mail " + $scope.email + " not found.";
                } else {
                    $scope.error_msg = data;
                    
                }
            });
        
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss();
    };
    
});


loginApp.constant('AUTH_EVENTS', {
    loginSuccess    : 'auth-login-success',
    loginFailed     : 'auth-login-failed',
    logoutSuccess   : 'auth-logout-success',
    sessionTimeout  : 'auth-session-timeout',
    notAuthenticated: 'auth-not-authenticated',
    notAuthorized   : 'auth-not-authorized'
  })
  
loginApp.factory('authHttpResponseInterceptor',['$q','$location', '$window', function($q, $location, $window, $rootScope){
    return {
        responseError: function(rejection) {
            if (rejection.status === 401) {
                $window.location.href = "#/login";
            }
            return $q.reject(rejection);
        }
    }
}]);

loginApp.config(['$httpProvider',function($httpProvider) {
    //Http Intercpetor to check auth failures for xhr requests
    $httpProvider.interceptors.push('authHttpResponseInterceptor');
}]);

//from https://github.com/TheSharpieOne/angular-input-match/blob/master/dist/angular-input-match.js
//used to test password confirmation on person-edit.html person-new.html
loginApp.directive('match', function($parse) {
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
