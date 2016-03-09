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
      .when('/tasks', params);

    /*
    THIS DOES NOT WORK SOMETIMES:

    $routeProvider
      .when('/tasks/:tab/', params);
    */
    $routeProvider
      .when('/tasks/unassigned', params);
    $routeProvider
      .when('/tasks/selfAssigned', params);
    $routeProvider
      .when('/tasks/tickets', params);
    $routeProvider
      .when('/tasks/all', params);
    $routeProvider
      .when('/tasks/finished', params);
  }
})();
