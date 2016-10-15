angular.module('app')
  .directive('placeEdit', function($window, $http, $modal, ServiceService, CatalogService, PlacesService) {

    return {
      restrict: 'E',
      templateUrl: 'app/common/components/form/directives/place/placeEdit.html',
      replace:true,
      link: function($scope) {

        var oService = ServiceService.oService;

        //TODO combine functions "serviceIsAvailable", "showAddService" and "showMessage" into one and call PlacesService.serviceAvailableIn() only once

        $scope.serviceIsAvailable = function(){
          var sa = PlacesService.serviceAvailableIn();
          if (sa.thisCountry || sa.thisRegion || sa.thisCity) {
            return true;
          }
          return false;
        };

        $scope.showAddCountryService = function(){
          var sa = PlacesService.serviceAvailableIn();
          if (sa.thisCountry || sa.thisRegion || sa.thisCity) {
            // service is available
            return false;
          }
          var regionIsChosen = PlacesService.regionIsChosen();
          var cityIsChosen =  PlacesService.cityIsChosen();
          return !regionIsChosen && !cityIsChosen && !sa.someRegion && !sa.someCityInThisRegion;
        };

        $scope.showCountryMessage = function(){
          var sa = PlacesService.serviceAvailableIn();
          var regionIsChosen = PlacesService.regionIsChosen();
          var cityIsChosen =  PlacesService.cityIsChosen();
          return !regionIsChosen && !cityIsChosen && (sa.someRegion || sa.someCityInThisRegion);
        };

        $scope.showAddRegionService = function(){
          var sa = PlacesService.serviceAvailableIn();
          var regionIsChosen = PlacesService.regionIsChosen();
          var cityIsChosen =  PlacesService.cityIsChosen();
          if (!regionIsChosen){
            return false;
          }
          if (sa.thisRegion || sa.thisCity) {
            // service is available
            return false;
          }
          if (regionIsChosen && !cityIsChosen){
            return !sa.someCityInThisRegion;
          }
          return false;
        };

        $scope.showRegionMessage = function(){
          var sa = PlacesService.serviceAvailableIn();
          var regionIsChosen = PlacesService.regionIsChosen();
          var cityIsChosen =  PlacesService.cityIsChosen();
          return regionIsChosen && !cityIsChosen && sa.someCityInThisRegion
        };

        $scope.showAddCityService = function(){
          var sa = PlacesService.serviceAvailableIn();
          if (sa.thisRegion || sa.thisCity) {
            // service is available
            return false;
          }
          var regionIsChosen = PlacesService.regionIsChosen();
          var cityIsChosen =  PlacesService.cityIsChosen();
          return regionIsChosen && cityIsChosen;
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
            if (!serviceData){
              serviceData = PlacesService.findServiceDataByCountry();
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
