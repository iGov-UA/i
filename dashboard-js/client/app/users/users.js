(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(usersConfig);

  usersConfig.$inject = ['$routeProvider'];
  function usersConfig($routeProvider) {
    $routeProvider
      .when('/users', {
        templateUrl: 'app/todo/todo.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
