var servicesApp = angular.module("servicesApp", []);

servicesApp.factory('auth_service', function($http, $window, $rootScope) {
    var authService = {};

    authService.login = function(person, callback) {
        return $http.post('svc/auth/login', person).
            success(function(status, data) {
//                console.log(data);
                console.log(status);
                callback(status, data);
            }).
            error(function(status, data) {
                console.log("auth error");
                console.log(status);
                console.log(data);
                $rootScope.error_msg = "e-mail or password incorrect. Message from server: " + data.error;
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

servicesApp.factory('MessageService', ['$rootScope', function($rootScope) {
    $rootScope.messages = [];

    var MessageService = function() {
        this.setMessages = function(messages) {
            console.log(messages);
            $rootScope.messages = messages;
        };

        this.hasMessages = function() {
            return $rootScope.messages && $rootScope.messages.length > 0;
        }

        this.clearMessages = function() {
            $rootScope.messages = [];
        }
    };

    return new MessageService();
}]);
