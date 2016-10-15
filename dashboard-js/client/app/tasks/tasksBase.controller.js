(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('TasksBaseCtrl', [
      '$scope',
      '$state',
      'tasksStateModel',
      function ($scope, $state, tasksStateModel) {
        $scope.tasksStateModel = tasksStateModel;
        if ($state.current.name == 'tasks')
          $state.go('tasks.typeof', {type: 'tickets'});
      }
    ]);
})();
