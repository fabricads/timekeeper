 /**
  * 
  */
 var timekeeperControllers = angular.module("timekeeperControllers");

 /**
  * Controler For The Project Consultant Page
  */
 timekeeperControllers.controller("project_associate_consultants", function($scope, $http, projectService, $routeParams,
                                                                   $filter, $log, consultantService) {
    $scope.selected=false;
    $scope.noResults = false;
    
    /**
     * retrieves project and taks data
     */
    projectService.getProject($routeParams.projectId).then(
        function(response){
            $scope.project=response.data;
            $log.debug("Achou um projeto com o id "+$routeParams.projectId);
            $log.debug($scope.project);
            $scope.selected_consultants = $scope.project.consultants;
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
        },
        function(error){
            $log.error("An error has occured while trying to retrieve consultants")
            $log.error(error.data);
        }
    )
    
    $scope.project_submit = function(project) {
        project.consultants = $scope.selected_consultants;
// project.tasksDTO = $scope.selected_tasks;
// project.tasksToRemove = $scope.tasks_to_remove;
        $http.post("/timekeeper/svc/project/associate-consultants", project).success(
            function(data, status, header, config) {
                $scope.saved = true;
                $scope.error_msg = null;
                $scope.prj_name = project.name;
            }).
            error(function(data, status, header, config) {
                $scope.error_msg = data;
            });
    };
    
    $scope.temp_consultant = {name: ""};
    $scope.temp_consultant.tasks = [];
    $scope.temp_task;
    
    $scope.add_consultant = function() {
        if ($scope.temp_consultant.id != null ) {
            found = $filter('findById')($scope.temp_consultant.tasks, $scope.temp_task.id);
            // add the task, if it is not already associated to the consultant
            if (found == null) {
                $scope.temp_consultant.dissociateOfProject = true;
                $scope.temp_consultant.tasks.push($scope.temp_task);
                found = $filter('findById')($scope.selected_consultants, $scope.temp_consultant.id);
                // add the consultant to the list, if not added previously
                if (found == null && $scope.temp_consultant.id != null ) {
                    $scope.selected_consultants.push($scope.temp_consultant);
                }
// console.log($scope.selected_consultants);

                // clean temp_consultant
                $scope.temp_consultant = {name: ""};
                $scope.temp_consultant.tasks = [];
                $scope.temp_task;
            }
        }
    };
    $scope.update=function(){
        $log.info("You have selected an item!!!");
        $scope.selected=true;
    };

    $scope.remove_consultant = function(consultant) {
        idx = $scope.selected_consultants.indexOf(consultant);
        if (idx > -1) {
            $scope.selected_consultants.splice(idx, 1);
        }
    };

    
});