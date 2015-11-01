angular.module('app')
  .directive('placeEdit', function($http, $modal, ServiceService, CatalogService, PlacesService) {

    return {
      restrict: 'E',
      templateUrl: 'app/common/components/form/directives/place/placeEdit.html',
      replace:true,
      link: function($scope) {

        var oService = ServiceService.oService;

        $scope.serviceIsAvailable = function(){
          var bAvailable = false;
          var sa = PlacesService.serviceAvailableIn();
          if (sa.thisRegion || sa.thisCity) {
            bAvailable = true;
          }
          return bAvailable;
        };

        $scope.openModal = function () {
          var modalInstance = $modal.open({
            animation: true,
            size: 'lg',
            templateUrl: 'app/common/components/form/directives/place/placeEditor.html',
            controller: 'PlaceEditorController'
          });

          modalInstance.result.then(function (editedServiceData) {

            var indexOf = oService.aServiceData
              .map(function(sd) { return sd.nID; })
              .indexOf(editedServiceData.nID);

            if (indexOf !== -1){
              oService.aServiceData[indexOf] = editedServiceData;
            } else {
              oService.aServiceData.push(editedServiceData);
            }

            ServiceService.set(oService)
              .then(function(){
              });
          });
        };

        $scope.remove = function (category) {
          var title = 'Видалення категоії';
          var message = 'Ви впевнені, що бажаєте видалити категорію ' + category.sName + '?';
          var numberOfSubcategories = category.aSubcategory.length;
          if (numberOfSubcategories > 1) {
            message = 'Ви намагєтесь видалити категорію з ' + numberOfSubcategories + ' підкатегоріями. ' + message;
          } else if (numberOfSubcategories === 1) {
            message = 'Ви намагєтесь видалити категорію з однією підкатегорію. ' + message;
          }

          var confirmationLevel = 0;
          if (numberOfSubcategories > 0) {
            confirmationLevel = 2;
          }

          openDeletionModal(title, message, confirmationLevel)
            .then(function () {
              CatalogService.removeCategory(category.nID, true)
                .then(function () {
                  initCatalogUpdate();
                });
            });
        }
      }
    };
  });
