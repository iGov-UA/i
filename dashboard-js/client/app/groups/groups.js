(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(groupsConfig);

  groupsConfig.$inject = ['$stateProvider'];
  function groupsConfig($stateProvider) {
    $stateProvider
      .state('groups', {
        url: '/groups',
        templateUrl: 'app/groups/groups.html',
        access: {
          requiresLogin: true
        }
      });
  }
})();
