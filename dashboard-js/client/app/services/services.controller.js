'use strict';

angular.module('dashboardJsApp')
  .controller('ServicesCtrl', function ($scope, $modal, schedule, bpForSchedule) {

    $scope.bp = bpForSchedule.bp;
    $scope.departments = [];

    $scope.bp.onChangeCallback = function () {
      $scope.$broadcast('bpChangedEvent');
      console.log($scope.bp.chosenBp);
      schedule.getFlowSlotDepartments($scope.bp.chosenBp.sID)
        .then(function (data) {
          $scope.departments = data;
        });
    };

    $scope.workHours = {
      getFunc: schedule.getSchedule,
      setFunc: schedule.setSchedule,
      deleteFunc: schedule.deleteSchedule
    };

    $scope.exemptions = {
      getFunc: schedule.getExemptions,
      setFunc: schedule.setExemption,
      deleteFunc: schedule.deleteExemption
    };

  });
