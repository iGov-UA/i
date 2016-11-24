'use strict';
angular.module('dashboardJsApp')
  .directive('selectField', function() {
    return {
      restrict: 'E',
      templateUrl: 'app/tasks/form-fields/selectField.html'
    };
});
