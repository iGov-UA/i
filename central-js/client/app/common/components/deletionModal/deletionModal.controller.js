'use strict';

angular.module('app')
  .controller('DeletionModalController', function($scope, $modalInstance, title, message, confirmation) {

    $scope.title = title;
    $scope.message = message;
    $scope.showSimpleConfirmation = confirmation === 1;
    $scope.showComplexConfirmation = confirmation === 2;

    $scope.delete = function () {
      $modalInstance.close();
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });
