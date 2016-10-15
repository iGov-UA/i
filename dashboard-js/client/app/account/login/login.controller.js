'use strict';

angular.module('dashboardJsApp')
  .controller('LoginCtrl', function(Auth, Idle, Modal, $scope, $location, $state) {
    $scope.user = {};
    $scope.errors = {};

    $scope.login = function(form) {
      $scope.submitted = true;
      $scope.authProcess = false;
      $scope.loggedIn = false;

      if (form.$valid) {
        $scope.authProcess = true;

        Auth.login({
            login: $scope.user.login,
            password: $scope.user.password
          })
          .then(function() {
            $scope.loggedIn = true;
            $state.go('tasks.typeof', {type:'unassigned'});
          })
          .catch(function(err) {
            $scope.authProcess = false;
            $scope.errors.other = err ? err.message : 'Невідома помилка';
          });
      }
    };
  });
