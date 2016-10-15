(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(['$stateProvider',
      function ($stateProvider) {
        $stateProvider
          .state('tools.services', {
            url: '/services',
            templateUrl: 'app/services/services.html',
            controller: 'ServicesCtrl',
            access: {
              requiresLogin: true
            }
          });
      }]);
})();
