(function ()
{
  'use strict';
  angular
    .module('dashboardJsApp')
    .controller('baTask', [
      '$scope',
      '$state',
      'taskData',
      'Auth',
      function ($scope, $state, taskData, Auth)
      {
        $scope.taskData = taskData;
        $scope.openRegularView = function() {
          console.log(window.history);
          if (window.history.length > 0)
            window.history.back();
          else {
            var type;
            if (!taskData.sLoginAssigned)
              type = 'unassigned';
            else if(taskData.sLoginAssigned == Auth.getCurrentUser().id)
              type = 'selfAssigned';
            else
              type = 'all';

            $state.go('tasks.typeof.view', {
              type: type,
              id: taskData.nID_Task
            });
          }
        };
      }
    ])
})();
