(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('NavbarCtrl', navbarCtrl);

  navbarCtrl.$inject = ['$scope', '$location', 'Auth', 'envConfigService', 'iGovNavbarHelper', 'tasksSearchService'];
  function navbarCtrl($scope, $location, Auth, envConfigService, iGovNavbarHelper, tasksSearchService) {
    $scope.menu = [{
      'title': 'Задачі',
      'link': '/tasks'
    }];

    envConfigService.loadConfig(function (config) {
      iGovNavbarHelper.isTest = config.bTest;
    });

    $scope.isAdmin = Auth.isAdmin;
    $scope.areInstrumentsVisible = false;
    $scope.iGovNavbarHelper = iGovNavbarHelper;

    $scope.isVisible = function(menuType){
      //$scope.menus = [{
      if(menuType === 'all'){
        return Auth.isLoggedIn() && Auth.hasOneOfRoles('manager', 'admin', 'kermit');
      }
      if(menuType === 'finished'){
        return Auth.isLoggedIn() && Auth.hasOneOfRoles('manager', 'admin', 'kermit', 'supervisor');
      }
      return Auth.isLoggedIn();
    };

    $scope.getCurrentUserName = function() {
      var user = Auth.getCurrentUser();
      return user.firstName + ' ' + user.lastName;
    };

    $scope.goToServices = function() {
      $location.path('/services');
    };

    $scope.goToTasks = function(tab) { console.log(tab);
      $location.path('/tasks/' + tab);
    };

    $scope.goToEscalations = function() {
      $location.path('/escalations');
    };

    $scope.goToReports = function () {
      $location.path('/reports');
    };

    $scope.logout = function() {
      Auth.logout();
      $location.path('/login');
    };

    $scope.isActive = function(route) {
      return route === $location.path();
    };

    $scope.goToUsers = function () {
      $location.path('/users');
    };

    $scope.goToGroups = function () {
      $location.path('/groups');
    };

    $scope.goToDeploy = function () {
      $location.path('/deploy');
    };

    $scope.goToProfile = function () {
      $location.path('/profile');
    };

    $scope.tasksSearch = {
      value: null,
      count: 0,
      loading: false,
      submited: false
    };

    $scope.searchInputKeyup = function ($event) {
      if ($event.keyCode === 13 && $scope.tasksSearch.value) {
        $scope.tasksSearch.loading=true;
        $scope.tasksSearch.count=0;
        $scope.tasksSearch.submited=true;
        tasksSearchService.searchTaskByUserInput($scope.tasksSearch.value)
          .then(function(aIds) {
            $scope.tasksSearch.count = aIds.length;
          })
          .finally(function() {
            $scope.tasksSearch.loading=false;
          });
      }
    }
  }
})();
