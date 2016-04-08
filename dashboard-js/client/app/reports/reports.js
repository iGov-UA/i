(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(['$stateProvider',
      function ($stateProvider) {
        $stateProvider
          .state('tools.reports', {
            url: '/reports',
            templateUrl: 'app/reports/reports.html',
            controller: 'ReportsCtrl'
          });
      }]);
})();
