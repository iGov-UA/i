'use strict';

angular.module('app')
  .controller('ErrorsModelController', ['$scope', '$modalInstance', 'error', function ($scope, $modalInstance, error) {
    $scope.error = error;
    //$scope.$filter = $filter;
    //, $filter

    $scope.close = function (el) {
      $modalInstance.close(el);
    };
  }]);
