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
      apiUrl: './api/subject-organ-join-tax'
    },
    ObjectEarthTarget: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/object-earth-target'
    }
  }
});
