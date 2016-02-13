(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(deployConfig);

  deployConfig.$inject = ['$routeProvider'];
  function deployConfig($routeProvider) {
    $routeProvider
      .when('/deploy', {
        templateUrl: 'app/todo/todo.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
