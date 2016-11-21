angular.module('iGovTable', ['autocompleteService', 'iGovMarkers', 'datepickerService'])
    .service('TableService',
        ['autocompletesDataFactory', 'ValidationService', 'DatepickerFactory',
        function (autocompletesDataFactory, ValidationService, DatepickerFactory) {

        var addTableFieldsProperties = function (formProps) {
        angular.forEach(formProps, function(prop) {
            if (prop.type === 'table') {
                angular.forEach(prop.aRow, function (fields) {
                    angular.forEach(fields.aField, function (item, key, obj) {

                        // добавляем примечания к полям, если они есть. проверка по разделителю ";"
                        var sFieldName = item.name || '';
                        var aNameParts = sFieldName.split(';');
                        var sFieldNotes = aNameParts[0].trim();
                        item.sFieldLabel = sFieldNotes;
                        sFieldNotes = null;
                        if (aNameParts.length > 1) {
                            sFieldNotes = aNameParts[1].trim();
                            if (sFieldNotes === '') {
                                sFieldNotes = null;
                            }
                        }
                        item.sFieldNotes = sFieldNotes;

                        var isExecutorSelect = item.name.split(';')[2];

                        if (item.type === 'date') {
                            obj[key].props = DatepickerFactory.prototype.createFactory();
                        }else if (item.type === 'select' || item.type === 'string' || isExecutorSelect && isExecutorSelect.indexOf('sID_SubjectRole=Executor') > -1) {
                            var match;
                            if (((match = item.id.match(/^s(Currency|ObjectCustoms|SubjectOrganJoinTax|ObjectEarthTarget|Country|ID_SubjectActionKVED|ID_ObjectPlace_UA)(_(\d+))?/)))
                                ||(item.type == 'select' && (match = item.id.match(/^s(Country)(_(\d+))?/))) || isExecutorSelect) {
                                if (match && autocompletesDataFactory[match[1]] && !isExecutorSelect) {
                                    item.type = 'select';
                                    item.selectType = 'autocomplete';
                                    item.autocompleteName = match[1];
                                    if (match[2])
                                        item.autocompleteName += match[2];
                                    item.autocompleteData = autocompletesDataFactory[match[1]];
                                } else if (!match && isExecutorSelect) {
                                    item.type = 'select';
                                    item.selectType = 'autocomplete';
                                    item.autocompleteName = 'SubjectRole';
                                    item.autocompleteData = autocompletesDataFactory[item.autocompleteName];
                                }
                            }
                        }
                        if (item.nWidth && item.nWidth.indexOf('%') === -1) {
                            if(item.nWidth.indexOf('px') === -1) item.nWidth = item.nWidth + 'px';
                        }
                    })
                })
            }
        });
    };

    // добавление свойства с максимальным к-вом строк таблицы (если лимит задан)
    var checkRowsLimit = function (formProps) {
        angular.forEach(formProps, function(item, key, obj) {
            if(item.type === 'table') {
                var isRowLimit = item.name.split(';');
                if(isRowLimit.length === 3 && isRowLimit[2].indexOf('nRowsLimit') !== -1) {
                    obj[key].nRowsLimit = isRowLimit[2].split('=')[1];
                }
            }
        })
    };

    /*
     * инициируем таблицу, передавая массив полей форм в качестве аргумента.
     * checkRowsLimit() - проверяет лимит на к-во строк таблицы.
     * addTableFieldsProperties() - для работы полей типа date, organJoin, select/autocomplete
     */

    this.init = function (formProps) {
        angular.forEach(formProps, function(item, key, obj) {
            if(item.type === 'table' && !/(\[id=(\w+)\])/.test(item.description)) {
                if(!item.aRow) {
                    item.aRow = [];
                }
                try {
                    var parsedTable = JSON.parse(item.value);
                        obj[key].aRow.push(parsedTable);
                    } catch (e) {
                    console.log('error message: ' + e)
                    }
                checkRowsLimit(formProps);
                addTableFieldsProperties(formProps);
            }
        });
    };

    /*
     * проверка поля на редактирование.
     * иногда передается false/true как строка 'false'/'true', поэтому включил данную проверку
     */

    this.isFieldWritable = function (field) {
        if(!field) {
            return true;
        } else{
            if(typeof field === 'string' || field instanceof String) {
                if(field === 'true') return true;
                if(field === 'false') return false;
            } else if (typeof field === 'boolean') {
                return field;
            }
        }
    };

    /*
    * проверка поля таблицы на видимость. принцип как и isFieldWritable
    * todo обьединить в общую функцию.
     */
    this.isVisible = function (field) {
      if('bVisible' in field) {
          if(typeof field === 'string' || field instanceof String) {
              if(field === 'true') return true;
              if(field === 'false') return false;
          } else if (typeof field === 'boolean') {
              return field;
          }
      } else {
          return true;
      }
    };

    /*
     * добавление новой строки в таблице, посредством копирования дефолтной строки (тк она образцовая),
     * addTableFieldsProperties() - для работы полей типа date, organJoin, select/autocomplete
     */

    this.addRow = function (id, form) {
        angular.forEach(form, function (item, key, obj) {
            if(item.id === id) {
                var defaultCopy = angular.copy(obj[key].aRow[0]);
                angular.forEach(defaultCopy.aField, function (field) {
                    if(field.default) {
                        delete field.default;
                    } else if(field.props) {
                        field.props.value = ""
                    }
                    field.value = "";
                });
                addTableFieldsProperties();
                obj[key].aRow.push(defaultCopy);
            }
        });
    };

    // удаление строки таблицы.
    this.removeRow = function (form, index, id) {
        angular.forEach(form, function (item, key, obj) {
            if (item.id === id) {
              obj[key].aRow.splice(index, 1);
            }
        });
    };

    // проверка ограничения на к-во строк в таблице (достиг ли лимит)
    this.rowLengthCheckLimit = function (table) {
        return table.aRow.length >= table.nRowsLimit
    };
}]);
