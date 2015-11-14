angular.module('app').controller('ServiceInstructionController', function($state, $rootScope, $scope, service, AdminService, ServiceService) {
  $scope.bAdmin = AdminService.isAdmin();
  $scope.bShowSpinner = false;
  function reInit(info){
    $scope.sInstruction = info;
    $scope.bShowInstruction = $scope.sInstruction.trim() !== '';

    $scope.bEditMode = false;
    $scope.sEditedInstruction = angular.copy($scope.sInstruction);
  }

  reInit(service.sInfo);

  $scope.edit = function(){
    $scope.bEditMode = true;
  };

  $scope.apply = function() {
    var serviceToSet = angular.copy(service);
    serviceToSet.sInfo = $scope.sEditedInstruction;
    $scope.bShowSpinner = true;
    ServiceService.set(serviceToSet)
      .then(function(updatedService){
          reInit(updatedService.sInfo);
          $scope.bShowSpinner = false;
        });
    $scope.bEditMode = false;
  };

  $scope.cancel = function(){
    $scope.bEditMode = false;
  };
});
