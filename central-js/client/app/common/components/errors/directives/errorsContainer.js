'use strict';

angular.module("app").directive("errorsContainer", function ($modal) {

  return {
    restrict: "A",
    controller: "ErrorsController",
    link: function ($scope) {
      $scope.$watch(function () {
        return $scope.errors.length;
      }, function (value) {
        if (value > 0) {
          var modalInstance = $modal.open({
            animation: true,
            size: 'md',
            templateUrl: 'app/common/components/errors/modal/errorsModal.html',
            controller: 'ErrorsModelController',
            resolve: {
              error: function () {
                return $scope.errors[0];
              }
            }
          });

          modalInstance.result.then(function (el) {
            $scope.errors.splice($scope.errors.indexOf(el), 1);
          });
        }
      });
    }
  }
})
;
