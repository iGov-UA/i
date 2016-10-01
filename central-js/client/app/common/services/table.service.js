angular.module('app')
  .service('TableService', function (DatepickerFactory, autocompletesDataFactory) {

    var addTableFieldsProperties = function (formProps) {
      angular.forEach(formProps, function(prop) {
        if (prop.type === 'table') {
          angular.forEach(prop.aRow, function (fields) {
            angular.forEach(fields.aField, function (item, key, obj) {

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

              if (item.type === 'date') {
                if (!item.props) {
                  obj[key].props = DatepickerFactory.prototype.createFactory();
                } else {
                  obj[key].props.open = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    this.opened = true;
                  };
                  obj[key].props.get = function () {
                    return $filter('date')(this.value, this.format);
                  };
                  obj[key].props.clear = function () {
                    this.value = null;
                  };
                  obj[key].props.today = function () {
                    this.value = new Date();
                  };
                  obj[key].props.isFit = function (property) {
                    return property.type === 'date';
                  };
                }
              } else if (item.type === 'select' || item.type === 'string') {
                var match;
                if (((item.type == 'string' || item.type == 'select')
                  && (match = item.id.match(/^s(Currency|ObjectCustoms|SubjectOrganJoinTax|ObjectEarthTarget|Country|ID_SubjectActionKVED|ID_ObjectPlace_UA)(_(\d+))?/)))
                  ||(item.type == 'select' && (match = item.id.match(/^s(Country)(_(\d+))?/)))) {
                  if (autocompletesDataFactory[match[1]]) {
                    item.type = 'select';
                    item.selectType = 'autocomplete';
                    item.autocompleteName = match[1];
                    if (match[2])
                      item.autocompleteName += match[2];
                    item.autocompleteData = autocompletesDataFactory[match[1]];
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

    this.init = function (formProps) {
      angular.forEach(formProps, function(item, key, obj) {
        if(item.type === 'table') {
          if(sessionStorage.getItem("TableParams") !== null){
          } else {
            if(!item.aRow) {
              item.aRow = [];
            }
            try {
              var parsedTable = JSON.parse(item.value);
              obj[key].aRow.push(parsedTable);
            } catch (e) {
              return
            }
          }
          checkRowsLimit(formProps);
          addTableFieldsProperties(formProps);
        }
      });
    };

    this.addRow = function (id, table) {
      angular.forEach(table, function (item, key, obj) {
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
    }
  });
