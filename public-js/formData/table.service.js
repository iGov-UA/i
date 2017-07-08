angular.module('iGovTable', ['autocompleteService', 'iGovMarkers', 'datepickerService'])
    .service('TableService',
        ['autocompletesDataFactory', 'ValidationService', 'DatepickerFactory', '$injector',
        function (autocompletesDataFactory, ValidationService, DatepickerFactory, $injector) {

        var factory = $injector.has('FileFactory') ? $injector.get('FileFactory') : null;

        var addTableFieldsProperties = function (formProps) {
        angular.forEach(formProps, function(prop) {
            if (prop.type === 'table') {
                angular.forEach(prop.aRow, function (fields) {
                    if(fields && fields.aField){
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
                        }else if(item.type === 'file' && factory !== null) {
                          var temp = obj[key];
                          obj[key] = new factory();
                          for(var k in temp) obj[key][k]=temp[k];
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
                            } else if (!match && isExecutorSelect.indexOf('SubjectRole') > -1) {
                              var props = isExecutorSelect.split(','), role;
                              item.type = 'select';
                              item.selectType = 'autocomplete';
                              for(var i=0; i<props.length; i++) {
                                if(props[i].indexOf('sID_SubjectRole') > -1) {
                                  role = props[i];
                                  break;
                                }
                              }
                              var roleValue = role ? role.split('=')[1] : null;
                              if(roleValue && roleValue === 'Executor') item.autocompleteName = 'SubjectRole';
                              if(roleValue && roleValue === 'ExecutorDepart') item.autocompleteName = 'SubjectRoleDept';
                              item.autocompleteData = autocompletesDataFactory[item.autocompleteName];
                            }
                          }
                        }
                        if (item.nWidth && item.nWidth.indexOf('%') === -1) {
                          if(item.nWidth.indexOf('px') === -1) item.nWidth = item.nWidth + 'px';
                        }
                      })
                    } else {
                      console.warn('В таблице "' + prop.name.split(';')[0] + '" [id=' + prop.id + '] в массиве строк отсутствуют элементы');
                    }
                })
            }
        });
    };

    // добавление свойства с максимальным к-вом строк таблицы (если лимит задан)
    var checkRowsLimit = function (formProps) {
        angular.forEach(formProps, function(item, key, obj) {
            if(item.type === 'table') {
                var hasOptions = item.name.split(';');
                if(hasOptions.length === 3) {
                    var hasLimit = hasOptions[2].match(/\b(nRowsLimit=(\d+))\b/);
                    if(hasLimit !== null)
                        obj[key].nRowsLimit = hasLimit[2];
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
                angular.forEach(defaultCopy.aField, function (field, k, o) {
                    if(field.type === 'file') {
                        var copy = field;
                        factory !== null ? o[k] = new factory() : o[k] = {};
                        o[k].required = copy.required;
                        o[k].type = copy.type;
                        o[k].name = copy.name;
                        o[k].writable = copy.writable;
                        o[k].id = copy.id + '_' + obj[key].aRow.length;
                    }
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