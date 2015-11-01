'use strict';

angular.module('app')
  .controller('PlaceEditorController', function($scope, $modalInstance, ServiceService, PlacesService) {

    var jsonContent = {};
    $scope.jsoneditorOptions = {
      mode: 'tree',
      modes: ['code', 'form', 'text', 'tree', 'view'],
      change: function (json){
        jsonContent = json;
      }
    };

    $scope.serviceTypes = [
      {
        nID: 1,
        sName: 'Зовнішня',
        sNote: 'Користувач переходить за посиланням на послугу, реалізовану на іншій платформі'
      },
      {
        nID: 2,
        sName: 'Авторизація',
        sNote: 'Користувач проходить верифікацію на порталі і переходить за посиланням на сторонню платформу, де реалізовано послугу'
      },
      {
        nID: 3,
        sName: 'Пов\'язана',
        sNote: 'На іншій платформі послуга реалізована так, що нею можна користуватися, не покидаючи Портал (через API)'
      },
      {
        nID: 4,
        sName: 'Вбудована',
        sNote: 'Всі етапи послуги реалізовані на платформі Порталу'
      }
    ];

    $scope.serviceData = PlacesService.findServiceDataByCity();

    if ($scope.serviceData){
      var currentServiceTypeId = $scope.serviceData.oServiceType.nID;

      var getCurrentServiceType = function(serviceTypeId){
        var filtered = $scope.serviceTypes.filter(function(type){
          return type.nID === serviceTypeId
        });
        return filtered[0];
      };

      $scope.serviceData.nID_ServiceType = getCurrentServiceType(currentServiceTypeId);
    } else {
      $scope.serviceData = {
        asAuth: 'BankID,EDS',
        nID_Server: 0,
        sURL: '',
        sNote: '',
        oData: {},
        //oSubject_Operator: {
        //  oSubject: {
        //    sID: "ПАО",
        //    sLabel: "ПАО ПриватБанк",
        //    sLabelShort: "ПриватБанк",
        //    nID: 1
        //  },
        //  sOKPO: "093205",
        //  sFormPrivacy: "ПАО",
        //  sNameFull: "Банк ПриватБанк",
        //  nID: 1,
        //  sName: "ПриватБанк"
        //}
      }
    }

    var addPlaceDataToServiceData = function(serviceData){
      var place = PlacesService.getPlaceData();
      var city = place && place.city;
      var region = place && place.region;

      if (!city || !region){
        throw "city or region is not defined";
      }

      serviceData.oCity = {
        nID: city.nID,
        sID_UA: city.sID_UA,
        sName: city.sName,
        oRegion: {
          nID: region.nID,
          sID_UA: region.sID_UA,
          sName: region.sName
        }
      };
      //serviceData.oPlace = {
      //  sID_UA: city.sID_UA,
      //  nID: city.nID,
      //  sName: city.sName,
      //  nID_PlaceType: 3,
      //  sNameOriginal: ''
      //};
      //serviceData.oPlaceRoot = {
      //  sID_UA: region.sID_UA,
      //  nID: 50000 +  region.nID,
      //  sName: region.sName,
      //  nID_PlaceType: 1,
      //  sNameOriginal: ''
      //};
    };

    $scope.save = function () {
      var oService = ServiceService.oService;
      var serviceData = angular.copy($scope.serviceData);
      serviceData.oService = {
        nID: oService.nID
      };
      serviceData.oData = angular.fromJson(jsonContent);
      addPlaceDataToServiceData(serviceData);
      $modalInstance.close(serviceData);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });
