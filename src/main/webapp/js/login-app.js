var loginApp = angular.module("loginApp", [ "ngRoute", "ngResource", "servicesApp"]);

loginApp.config([ "$routeProvider", function($routeProvider) {
	
	$routeProvider.
	when("/", {
	    templateUrl : "login.html",
	}).
	
    otherwise({
		redirectTo : "/"
	});
	
}

]);

loginApp.controller("login_ctrl", function($scope, $rootScope, $location, $window, AUTH_EVENTS, auth_service) {

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
                }
            } else {
                console.log(">> login failed");
                $scope.error_msg = "E-mail or password incorrect.";
                $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
            }
        });
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
                $window.location.href = "login.html";
            }
            return $q.reject(rejection);
        }
    }
}]);

loginApp.config(['$httpProvider',function($httpProvider) {
    //Http Intercpetor to check auth failures for xhr requests
    $httpProvider.interceptors.push('authHttpResponseInterceptor');
}]);
