(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(groupsConfig);

  groupsConfig.$inject = ['$routeProvider'];
  function groupsConfig($routeProvider) {
    $routeProvider
      .when('/groups', {
        templateUrl: 'app/groups/groups.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
