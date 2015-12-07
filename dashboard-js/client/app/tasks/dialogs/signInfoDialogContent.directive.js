'use strict';

angular.module('dashboardJsApp').directive('signInfoContentDialog', [
  function() {
    return {
      restrict: 'E',
      scope: false,
      templateUrl: 'app/tasks/dialogs/signInfoDialogContent.html'
    };
  }
]);
