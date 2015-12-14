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
      apiUrl: './api/subject-organ-join-tax',
      filter: function (scope, item) {
        return scope.formData.params.sID_Public_SubjectOrganJoin
          && scope.formData.params.sID_Public_SubjectOrganJoin.value == item.nID_SubjectOrganJoin;
      }
    },
    ObjectEarthTarget: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/object-earth-target'
    }
  }
});
