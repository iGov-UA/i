angular.module('autocompleteService', [])
    .factory('autocompletesDataFactory', function () {
    return {
        Currency: {
            valueProperty: 'sName_UA',
            titleProperty: 'sFind',
            prefixAssociatedField: 'sID_UA',
            apiUrl: './api/currencies',
            orderBy: 'sName_UA'
        },
        ObjectCustoms: {
            valueProperty: 'sName_UA',
            titleProperty: 'sFind',
            prefixAssociatedField: 'sID_UA',
            orderBy: 'sName_UA',
            apiUrl: './api/object-customs',
            hasPaging: true
        },
        SubjectRole: {
            apiUrl: './api/subject-role',
            titleProperty: 'sFind',
            orderBy: 'sLogin',
            prefixAssociatedField: 'sLogin',
            valueProperty: 'sLogin'
        },
        SubjectOrganJoinTax: {
            valueProperty: 'sName_UA',
            titleProperty: 'sName_UA',
            orderBy: 'sID_UA',
            prefixAssociatedField: 'sID_UA',
            apiUrl: './api/subject/organs/join-tax',
            init: function (scope) {
                // данный $watch нужен для полей в таблице
                var form = scope.activitiForm ? scope.activitiForm.formProperties : scope.taskForm;
                angular.forEach(form, function (table, tableKey) {
                    if(table.type === 'table') {
                        angular.forEach(table.aRow, function (row, rowKey) {
                            angular.forEach(row.aField, function (field, fieldKey) {
                                if(field.id === 'sID_Public_SubjectOrganJoin') {
                                    scope.$watch('activitiForm.formProperties['+ tableKey +'].aRow['+ rowKey + '].aField[' + fieldKey + '].nID', function (newValue) {
                                        scope.refreshList('nID_SubjectOrganJoin', newValue);
                                    })
                                }
                            });
                        });
                    }
                });
                // данный $watch нужен для формы
                scope.$watch("formData.params['sID_Public_SubjectOrganJoin'].nID", function (newValue) {
                    scope.refreshList('nID_SubjectOrganJoin', newValue);
                });
                if(scope.taskForm){
                    angular.forEach(scope.taskForm, function (item, key) {
                        if(item.id === 'sID_Public_SubjectOrganJoin') {
                            scope.$watch("taskForm["+ key +"].nID", function (newValue) {
                                scope.refreshList('nID_SubjectOrganJoin', newValue);
                            });
                        }
                    })
                }
            }
        },
        ObjectEarthTarget: {
            valueProperty: 'sName_UA',
            titleProperty: 'sFind',
            orderBy: 'sName_UA',
            prefixAssociatedField: 'sID_UA',
            apiUrl: './api/object-earth-target'
        },
        Country: {
            valueProperty: 'sNameShort_UA',
            titleProperty: 'sFind',
            orderBy: 'sNameShort_UA',
            prefixAssociatedField: 'nID_UA',
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
