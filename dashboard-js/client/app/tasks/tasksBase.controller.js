(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('TasksBaseCtrl', [
      '$state',
      function ($state) {
        if ($state.current.name == 'tasks')
          $state.go('tasks.typeof', {type: 'tickets'});
      }
    ]);
})();
