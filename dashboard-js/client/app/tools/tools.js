(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config([
      '$stateProvider',
      function ($stateProvider) {
        $stateProvider
          .state('tools', {
            abstract: true,
            templateUrl: 'app/tools/tools.html',
            access: {
              requiresLogin: true
            }
          });
      }
    ]);
})();
