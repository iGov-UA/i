(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(deployConfig);

  deployConfig.$inject = ['$routeProvider'];
  function deployConfig($routeProvider) {
    $routeProvider
      .when('/deploy', {
        templateUrl: 'app/deploy/deploy.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
