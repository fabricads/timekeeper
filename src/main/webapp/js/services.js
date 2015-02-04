var servicesApp = angular.module("servicesApp", []);

servicesApp.factory('auth_service', function($http, $window, $rootScope) {
    var authService = {};

    authService.login = function(person, callback) {
        return $http.post('svc/auth/login', person).
            success(function(data, status) {
//                console.log(data);
//                console.log(status);
                callback(status, data);
            });
    };

    authService.logout = function(callback) {
        return $http.post('svc/auth/logout').
            success(function(data, status) {
//                console.log(data);
//                console.log(status);
                callback(status, data);
            });
    };

    authService.isAuthenticated = function() {
        return $rootScope.user != null;
    };

/*
 *     authService.isAuthorized = function(authorizedRoles) {
        if (!angular.isArray(authorizedRoles)) {
            authorizedRoles = [ authorizedRoles ];
        }
        return (authService.isAuthenticated() && authorizedRoles.indexOf(Session.roles) !== -1);
    };
*/
    return authService;
});
