angular.module('app').factory('autocompletesDataFactory', function () {
  return {
    Currency: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/currencies'
    },
    ObjectCustoms: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/object-customs?sName_UA'
    },
    SubjectOrganJoinTax: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/subject/organs/join-tax',
      link: function (scope) {
        scope.$watch("formData.params['sID_Public_SubjectOrganJoin'].nID", function (newValue) {
          scope.resetAutoComplete();
          if (newValue)
            scope.dataList.load(scope.serviceData, null, {nID_SubjectOrganJoin: newValue}).then(function (regions) {
              scope.dataList.initialize(regions);
            });
        });
      }
    },
    ObjectEarthTarget: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/object-earth-target'
    },
    Country: {
      valueProperty: 'nID_UA',
      titleProperty: 'sNameShort_UA',
      additionalValueProperty: 'sID_UA',
      apiUrl: './api/countries/'
    }
  }
});
