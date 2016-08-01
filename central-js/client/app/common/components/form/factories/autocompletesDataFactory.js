angular.module('app').factory('autocompletesDataFactory', function () {
  return {
    Currency: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/currencies',
      orderBy: 'sName_UA'
    },
    ObjectCustoms: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      apiUrl: './api/object-customs',
      hasPaging: true
    },
    SubjectOrganJoinTax: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      orderBy: 'sName_UA',
      apiUrl: './api/subject/organs/join-tax',
      init: function (scope) {
        scope.$watch("formData.params['sID_Public_SubjectOrganJoin'].nID", function (newValue) {
          scope.refreshList('nID_SubjectOrganJoin', newValue);
        });
      }
    },
    ObjectEarthTarget: {
      valueProperty: 'sID_UA',
      titleProperty: 'sName_UA',
      orderBy: 'sName_UA',
      apiUrl: './api/object-earth-target'
    },
    Country: {
      valueProperty: 'nID_UA',
      titleProperty: 'sNameShort_UA',
      orderBy: 'sNameShort_UA',
      additionalValueProperty: 'sID_UA',
      apiUrl: './api/countries/'
    },
    ID_SubjectActionKVED: {
      valueProperty: 'sID',
      titleProperty: 'sFind',
      orderBy: 'nID',
      apiUrl: './api/subject-action-kved'
    },
    ID_ObjectPlace_UA: {
      valueProperty: 'sID',
      titleProperty: 'sFind',
      orderBy: 'nID',
      apiUrl: './api/object-place'
    }
  }
});
