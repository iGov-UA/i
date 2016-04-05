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

  tasksConfig.$inject = ['$stateProvider'];
  function tasksConfig($stateProvider) {
    $stateProvider
      .state('tasks', {
        url: '/tasks',
        controller: 'TasksBaseCtrl',
        access: {
          requiresLogin: true
        },
        templateUrl: 'app/tasks/base.html'
      })
      .state('tasks.typeof', {
        url: '/:type',
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      })
      .state('tasks.typeof.view', {
        parent: 'tasks',
        url: '/:type/:id',
        templateUrl: 'app/tasks/tasks.html',
        controller: 'TasksCtrl',
        access: {
          requiresLogin: true
        }
      });
  }
})();
