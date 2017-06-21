 /**
  * 
  */
 var timekeeperControllers = angular.module("timekeeperControllers");

 /**
  * Controler For The Project Consultant Page
  */
 timekeeperControllers.controller("project_associate_consultants", function($scope, $timeout, $http, projectService, $routeParams,
                                                                   $filter, $log, consultantService) {
    $scope.selected=false;
    $scope.noResults = false;
    $scope.temp_consultant = {name: ""};
    //$scope.temp_consultant.tasks = [];
    $scope.temp_task={consultants:[]};

    function limpar(){
        $scope.temp_consultant = {name: ""};
        //$scope.temp_consultant.tasks = [];
        $scope.temp_task={consultants:[]};
        $scope.selected=false
        $timeout(function(){
            $scope.save=false;
        },500);
    }limpar();

    
    /**
     * retrieves project and taks data
     */
    projectService.getProject($routeParams.projectId).then(
        function(response){
            $scope.project=response.data;
            $log.debug("Achou um projeto com o id "+$routeParams.projectId);
            $log.debug($scope.project);
            //$scope.selected_consultants = $scope.project.consultants;
            projectService.getTaks($scope.project.id).then(
                function(response){
                     $scope.tasks = response.data;
                },
                function(error)
                {
                    $scope.error_msg = error.data;
                }
            )
        },
        function(error){
             $scope.error_msg = error.data;
        }
    )

    /**
     * Retrieve all consultants
     */
    consultantService.getAll().then(
        function(response){
            $scope.consultants=response.data;
            $log.debug("Find the following consultants: ");
            $log.debug( $scope.consultants);
        },
        function(error){
            $log.error("An error has occured while trying to retrieve consultants")
            $log.error(error.data);
        }
    )
    
    $scope.project_submit = function() {
        $log.debug($scope.temp_task);
        $http.post("svc/project/associate-consultants", $scope.temp_task).success(
            function(data, status, header, config) {
                $scope.saved = true;
                $scope.error_msg = null;
                limpar();
                //$scope.prj_name = project.name;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
    };
        
    $scope.add_consultant = function() {
        if ($scope.temp_consultant.id != null ) {
            found = $filter('findById')($scope.temp_task.consultants, $scope.temp_consultant.id);
            // add the consultant to the list, if not added previously
            if (found == null && $scope.temp_consultant.id != null ) {
                $scope.temp_task.consultants.push($scope.temp_consultant);
            }
            $scope.temp_consultant = {name: ""};
        }
 
    };
    
    $scope.update=function(){
        $log.info("You have selected an item!!!");
        $scope.selected=true;
        projectService.getTask($scope.project.id,$scope.temp_task.id).then(function(response){
            $log.debug(response);
            $scope.temp_task=response.data;
            //$scope.selected_consultants=response.data.consultants;
        })
    };

    $scope.cancel=function(){
        limpar();
    }
    $scope.remove_consultant = function(consultant) {
        idx = $scope.temp_task.consultants.indexOf(consultant);
        if (idx > -1) {
            $scope.temp_task.consultants.splice(idx, 1);
        }
    };

    
});