(function () {
    'use strict';

    angular
      .module('dashboardJsApp')
      .config(shareConfig);

  shareConfig.$inject = ['$stateProvider'];
    function shareConfig($stateProvider) {
      $stateProvider
        .state('share', {
          url: '/share/:nID&:sSecret',
          views: {
            '@': {
              templateUrl: 'app/share/share.html',
              controller: 'ShareCtrl'
            }
          }
        })
     }
  })();
