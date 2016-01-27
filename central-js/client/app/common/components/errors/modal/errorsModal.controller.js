'use strict';

angular.module('app')
  .controller('ErrorsModelController', function ($scope, $modalInstance, error, $filter) {
    $scope.error = error;

    $scope.close = function (el) {
      $modalInstance.close(el);
    };
  });
