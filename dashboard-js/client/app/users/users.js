(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(usersConfig);

  usersConfig.$inject = ['$routeProvider'];
  function usersConfig($routeProvider) {
    $routeProvider
      .when('/users', {
        templateUrl: 'app/users/users.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
