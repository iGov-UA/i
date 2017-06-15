angular.module('dashboardJsApp').controller('fulfillmentCtrl', ['$scope', 'DocumentsService', 'oTask', 'taskData',
  'lunaService', 'fieldsService', 'eaTreeViewFactory',
  function ($scope, DocumentsService, oTask, taskData, lunaService, fieldsService, eaTreeViewFactory) {
    $scope.selectedTask = oTask;
    $scope.taskData = taskData;
    $scope.tabHistoryAppeal = 'fulfillment';
    $scope.attachIsLoading = true;

    DocumentsService.getProcessSubjectTree($scope.selectedTask.processInstanceId).then(function (res) {
      $scope.documentFullHierarchy = res;
      $scope.attachIsLoading = false;
      eaTreeViewFactory.setItems($scope.documentFullHierarchy.aProcessSubjectTree, $scope.$id);
    });

    $scope.creationDateFormatted = function (date) {
      return fieldsService.creationDateFormatted(date);
    };
}]);
