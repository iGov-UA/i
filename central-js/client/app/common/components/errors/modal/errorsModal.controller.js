'use strict';

angular.module('app')
  .controller('ErrorsModelController', function ($scope, $modalInstance, error) {
    $scope.error = error;
    //$scope.$filter = $filter;
    //, $filter

    $scope.close = function (el) {
      $modalInstance.close(el);
    };
  });
