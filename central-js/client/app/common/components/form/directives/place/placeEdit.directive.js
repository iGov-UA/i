angular.module('app')
  .directive('placeEdit', function($window, $http, $modal, ServiceService, CatalogService, PlacesService) {

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

        var openModal = function (bAddingNewPlace) {
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
                if(bAddingNewPlace){
                  $scope.$emit('onPlaceWasAdded');
                }else{
                  $scope.$emit('onPlaceChange');
                }
              });
          });
        };

        $scope.add = function(){
          openModal(true);
        };

        $scope.edit = function(){
          openModal(false);
        };

        $scope.remove = function () {

          var place = PlacesService.getPlaceData();

          var modalInstance = $modal.open({
            animation: true,
            size: 'md',
            templateUrl: 'app/common/components/deletionModal/deletionModal.html',
            controller: 'DeletionModalController',
            resolve: {
              title: function() {
                return 'Видалення інформації про сервіс \"' + oService.sName + '\"';
              },
              message: function() {

                var name = place.city && place.city.sName;
                if (!name){
                  name = place.region && place.region.sName;
                }
                return 'Ви впевнені, що бажаєте видалити інформацію про сервіс \"'
                  + oService.sName
                  + '\" у регіоні або населенному пункті \"'
                  + name
                  + '\"?';
              },
              confirmation: function(){
                return 0;
              }
            }
          });

          modalInstance.result.then(function () {
            var serviceData = PlacesService.findServiceDataByCity();
            if (!serviceData){
              serviceData = PlacesService.findServiceDataByRegion();
            }

            ServiceService.remove(serviceData.nID, true)
              .then(function () {
                $window.location.reload();
              });
          });
        }
      }
    };
  });
