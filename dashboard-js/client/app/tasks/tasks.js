(function() {
  'use strict';

  var params = {
    templateUrl: 'app/tasks/tasks.html',
    controller: 'TasksCtrl',
    access: {
      requiresLogin: true
    }
  };

  angular
    .module('dashboardJsApp')
    .config(tasksConfig);

  tasksConfig.$inject = ['$routeProvider'];
  function tasksConfig($routeProvider) {
    $routeProvider
      .when('/tasks/', params);

    $routeProvider
      .when('/tasks/:tab/', params);
  }
})();
