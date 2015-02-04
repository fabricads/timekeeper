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

    $scope.login = function(person) {
        auth_service.login(person, function(status, data) {
            if (status == 200) {
                if (data.email != null) { 
                    console.log("login ok");
                    console.log(data);
                    $rootScope.user = data;
    //                $window.sessionStorage["user"] = JSON.stringify(user);
                    $window.sessionStorage["user"] = data;
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

loginApp.controller("logout_ctrl", function($scope, $rootScope, AUTH_EVENTS, auth_service, Session) {

    $scope.logout = function() {
        auth_service.login(function(status, data) {
            if (status == 200) {
                console.log("logout ok");
                console.log(data);
                $rootScope.user = null;
            } else {
                console.log("logout failed");
                console.log(status);
                console.log(data);
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