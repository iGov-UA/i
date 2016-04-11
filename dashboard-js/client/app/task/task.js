(function () {
  'use strict';
  angular
    .module('dashboardJsApp')
    .config([
      '$stateProvider',
      function ($stateProvider) {
        $stateProvider
          .state('baTask', {
            url: '/task',
            controller: 'baTask',
            access: {
              requiresLogin: true
            },
            templateUrl: 'app/task/baTask.html',
            resolve: {
              taskData: [
                'tasks',
                '$location',
                function (tasks, $location) {
                  var params = angular.copy($location.search());
                  if (params.nID_Order) {
                    params.nID_Process = params.nID_Order.substring(params.nID_Order.length - 1);
                  }
                  return tasks.getTaskData(params, true);
                }
              ]
            }
          })
      }
    ]);
})();
