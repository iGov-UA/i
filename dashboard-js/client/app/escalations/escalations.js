(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(['$stateProvider',
      function ($stateProvider) {
        $stateProvider
          .state('tools.escalations', {
            url: '/escalations',
            templateUrl: 'app/escalations/escalations.html',
            controller: 'EscalationsCtrl',
            access: {
              requiresLogin: true
            }
          });
      }]);
})();
