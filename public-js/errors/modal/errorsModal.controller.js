'use strict';

angular.module('iGovErrors')
  .controller('ErrorsModelController', function ($scope, $modalInstance, error) {
    $scope.error = error;
    $scope.error.isNoSuccessType = $scope.error.sType !== 'success';

    //$scope.$filter = $filter;
    //, $filter

    $scope.close = function (el) {
      $modalInstance.close(el);
    };
  });
