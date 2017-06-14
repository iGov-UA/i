angular.module('dashboardJsApp').controller('historyCtrl', ['$scope', 'taskData', 'lunaService', 'fieldsService',
  function ($scope, taskData, lunaService, fieldsService) {
    $scope.taskData = taskData;
    $scope.tabHistoryAppeal = 'history';

    $scope.getMessageFileUrl = function (oMessage, oFile) {
      if(oMessage && oFile)
        return './api/tasks/' + $scope.nID_Process + '/getMessageFile/' + oMessage.nID + '/' + oFile.sFileName;
    };

    $scope.tabHistoryAppealChange = function (param) {
      $scope.tabHistoryAppeal = param;
    };

    $scope.creationDateFormatted = function (date) {
      return fieldsService.creationDateFormatted(date);
    };
  }]);
