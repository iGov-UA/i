(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('NavbarCtrl', navbarCtrl);

  navbarCtrl.$inject = ['$scope', '$location', 'Auth', 'envConfigService', 'iGovNavbarHelper'];
  function navbarCtrl($scope, $location, Auth, envConfigService, iGovNavbarHelper) {
    $scope.menu = [{
      'title': 'Задачі',
      'link': '/tasks'
    }];

    envConfigService.loadConfig(function (config) {
      iGovNavbarHelper.isTest = config.bTest;
    });

    $scope.isLoggedIn = Auth.isLoggedIn;
    $scope.isAdmin = Auth.isAdmin;
    $scope.areInstrumentsVisible = false;
    $scope.iGovNavbarHelper = iGovNavbarHelper;

    $scope.isVisible = function(menuType){
      //$scope.menus = [{
      if(menuType === 'all'){
        return  Auth.isLoggedIn() && Auth.hasOneOfRoles('manager', 'admin', 'kermit');
      }
      if(menuType === 'finished'){
        return  Auth.isLoggedIn() && Auth.hasOneOfRoles('manager', 'admin', 'kermit', 'supervisor');
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

    $scope.goToTasks = function() {
      $location.path('/tasks');
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
  }
})();
