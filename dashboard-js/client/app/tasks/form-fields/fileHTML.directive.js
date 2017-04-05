'use strict';
angular.module('dashboardJsApp')
  .directive('fileHtml', function() {
    return {
      restrict: 'E',
      templateUrl: 'app/tasks/form-fields/fileHTML.html'
    };
  });
