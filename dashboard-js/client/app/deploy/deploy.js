(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(deployConfig);

  deployConfig.$inject = ['$stateProvider'];
  function deployConfig($stateProvider) {
    $stateProvider
      .state('deploy', {
        url: '/deploy',
        templateUrl: 'app/deploy/deploy.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
