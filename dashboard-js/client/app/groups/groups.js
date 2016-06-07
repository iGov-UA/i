(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(groupsConfig);

  groupsConfig.$inject = ['$stateProvider'];
  function groupsConfig($stateProvider) {
    $stateProvider
      .state('tools.groups', {
        url: '/groups',
        templateUrl: 'app/groups/groups.html',
        controller: 'GroupsCtrl',
        access: {
          requiresLogin: true
        }
      });
  }
})();
