(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(['$stateProvider',
      function ($stateProvider) {
        $stateProvider
          .state('services', {
            url: '/services',
            templateUrl: 'app/services/services.html',
            controller: 'ServicesCtrl',
            access: {
              requiresLogin: true
            }
          });
      }]);
})();
