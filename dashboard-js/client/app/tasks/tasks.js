(function() {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(tasksConfig);

  tasksConfig.$inject = ['$routeProvider'];
  function tasksConfig($routeProvider) {
    $routeProvider
      .when('/tasks', {
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      });

    /*
    THIS DOES NOT WORK SOMETIMES:

    $routeProvider
      .when('/tasks/:tab/', params);
    */

    $routeProvider
      .when('/tasks/unassigned', {
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      });
    $routeProvider
      .when('/tasks/selfAssigned', {
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      });
    $routeProvider
      .when('/tasks/tickets', {
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      });
    $routeProvider
      .when('/tasks/all', {
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      });
    $routeProvider
      .when('/tasks/finished', {
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      });
  }
})();
