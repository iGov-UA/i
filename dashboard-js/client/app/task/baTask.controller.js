(function ()
{
  'use strict';
  angular
    .module('dashboardJsApp')
    .controller('baTask', [
      '$scope',
      'taskData',
      function ($scope, taskData)
      {
        $scope.taskData = taskData;
      }
    ])
})();
