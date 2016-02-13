(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(profileConfig);

  profileConfig.$inject = ['$routeProvider'];
  function profileConfig($routeProvider) {
    $routeProvider
      .when('/profile', {
        templateUrl: 'app/profile/profile.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
