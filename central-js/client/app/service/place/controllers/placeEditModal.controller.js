'use strict';

angular.module('app')
  .controller('PlaceEditModalController', function($scope, $modalInstance, PlacesService, entityToEdit) {

    $scope.jsoneditorOptions = {};
    $scope.serviceData = PlacesService.findServiceDataByCity();
    $scope.isNew = entityToEdit === undefined;
    $scope.entity = entityToEdit || {};

    $scope.save = function () {
      $modalInstance.close($scope.entity);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });
