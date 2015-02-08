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
                    console.log(data);
                }
            } else {
                console.log("login failed");
                console.log(status);
                console.log(data);
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
  
loginApp.factory('authHttpResponseInterceptor',['$q','$location', '$window', 'MessageService', function($q, $location, $window, $rootScope, MessageService){
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
                $rootScope.error_msg = rejection.data.message;
            } else if (rejection.status === 403) {
                MessageService.setMessages(rejection.data.message);
                console.log("Response Error 403", rejection);
            }
            return $q.reject(rejection);
        }
    }
}]);

loginApp.config(['$httpProvider',function($httpProvider) {
    //Http Intercpetor to check auth failures for xhr requests
    $httpProvider.interceptors.push('authHttpResponseInterceptor');
}]);
