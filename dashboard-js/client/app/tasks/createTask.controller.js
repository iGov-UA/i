angular.module('dashboardJsApp').controller('createTask',
  ['$scope', '$rootScope', 'tasks', '$state', 'ValidationService', 'TableService', 'Modal', 'iGovNavbarHelper',

  function ($scope, $rootScope, tasks, $state, ValidationService, TableService, Modal, iGovNavbarHelper) {

    var isItemInLS = localStorage.getItem('creating');

    $scope.selectedTask = JSON.parse(localStorage.getItem('selected-task'));

    if(isItemInLS) {
      $scope.creatingTask = JSON.parse(localStorage.getItem('creating'));
    }

    if ($scope.creatingTask.aFormProperty) {
      $scope.isSuccessfullySubmitted = false;
      $scope.isInProcess = false;
    }

    $scope.isTaskSuccessfullySubmitted = function () {
      if ($scope.creatingTask.aFormProperty) {
        if ($scope.isSuccessfullySubmitted !== undefined && $scope.isSuccessfullySubmitted)
          return true;
      }
      return false;
    };

    $scope.lightweightRefreshAfterSubmit = function () {
      iGovNavbarHelper.loadTaskCounters();
      $scope.isInProcess = false;
      $scope.isSuccessfullySubmitted = true;
    };

    $scope.upload = function (files, propertyID) {
      $rootScope.switchProcessUploadingState();
      tasks.upload(files, null, propertyID, true).then(function (result) {
        var filterResult = $scope.creatingTask.aFormProperty.filter(function (property) {
          return property.id === propertyID;
        });

        if(filterResult.length === 0) {
          for(var j=0; j<$scope.creatingTask.aFormProperty.length; j++) {
            if($scope.creatingTask.aFormProperty[j].type === 'table') {
              for(var c=0; c<$scope.creatingTask.aFormProperty[j].aRow.length; c++) {
                var row = $scope.creatingTask.aFormProperty[j].aRow[c];
                for(var i=0; i<row.aField.length; i++) {
                  if (row.aField[i].id === propertyID) {
                    filterResult.push(row.aField[i]);
                    break
                  }
                }
              }
            }
          }
        }
        if (filterResult && filterResult.length === 1) {
          if(result.response.sKey) {
            filterResult[0].value = JSON.stringify(result.response);
            filterResult[0].fileName = result.response.sFileNameAndExt;
            filterResult[0].signInfo = result.signInfo;
          } else{
            filterResult[0].value = result.response.id;
            filterResult[0].fileName = result.response.name;
            filterResult[0].signInfo = result.signInfo;
          }
        }
        $rootScope.switchProcessUploadingState();
      }).catch(function (err) {
        Modal.inform.error()('Помилка. ' + err.code + ' ' + err.message);
      });
    };

    $scope.sFieldLabel = function (sField) {
      var s = '';
      if (sField !== null) {
        var a = sField.split(';');
        s = a[0].trim();
      }
      return s;
    };

    $scope.unpopulatedFields = function () {
      if ($scope.creatingTask.aFormProperty) {
        var unpopulated = $scope.creatingTask.aFormProperty.filter(function (item) {
          return (item.value === undefined || item.value === null || item.value.trim() === "") && item.required;//&& item.type !== 'file'
        });
        return unpopulated;
      } else {
        return [];
      }
    };

    $scope.isFormInvalid = false;
    $scope.submit = function (form) {
      if(form.$invalid){
        $scope.isFormInvalid = true;
        return;
      } else {
        $scope.isFormInvalid = false;
      }
      if ($scope.creatingTask.aFormProperty) {
        $scope.isSubmitted = true;
        var unpopulatedFields = $scope.unpopulatedFields();
        if (unpopulatedFields.length > 0) {
          var errorMessage = 'Будь ласка, заповніть поля: ';
          if (unpopulatedFields.length === 1) {
            var nameToAdd = unpopulatedFields[0].name;
            if (nameToAdd.length > 50) {
              nameToAdd = nameToAdd.substr(0, 50) + "...";
            }
            errorMessage = "Будь ласка, заповніть полe '" + nameToAdd + "'";
          } else {
            unpopulatedFields.forEach(function (field) {
              var nameToAdd = field.name;
              if (nameToAdd.length > 50) {
                nameToAdd = nameToAdd.substr(0, 50) + "...";
              }
              errorMessage = errorMessage + "'" + nameToAdd + "',<br />";
            });
            var comaIndex = errorMessage.lastIndexOf(',');
            errorMessage = errorMessage.substr(0, comaIndex);
          }
          console.error(errorMessage);
          setTimeout(function () {
            angular.element('.submitted').first().focus();
          },100);
          return;
        }

        $scope.isInProcess = true;
        var id = $scope.creatingTask.processDefinitionId.split(':')[0];

        tasks.submitNewCreatedTask($scope.creatingTask, id)
          .then(function (result) {
            if(result.status === 500){
              var message = result.data.message;
              var errMsg = (message.indexOf("errMsg") >= 0) ? message.split(":")[1].split("=")[1] : message;
              $scope.isInProcess = false;
              Modal.inform.error(function (result) {
              })(errMsg + " " + (result && result.length > 0 ? (': ' + result) : ''));
            } else {
              var sMessage = "Форму відправлено.";
              var createdTaskNumber = result.nID_Task;
              Modal.inform.success(function (result) {
                $state.go('tasks.typeof.view', {type:'unassigned', id:createdTaskNumber});
              })(sMessage + " " + (result && result.length > 0 ? (': ' + result) : ''), $scope.lightweightRefreshAfterSubmit());
            $scope.$emit('task-submitted');
            }
          })
      }
    };

    $scope.backToTab = function () {
      $scope.creatingTask = null;
      var currentTab = $state.params.type;
      $state.go('tasks.typeof', {type: currentTab});
    };

    TableService.init($scope.creatingTask.aFormProperty);

    function fixName(item) {
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
    }

    var fixFieldsForTable = function (table) {
      var tableRow;
      fixName(table);
      if('content' in table){
        tableRow = table.content;
      } else {
        tableRow = table.aRow;
      }
      angular.forEach(tableRow, function (row) {
        angular.forEach(row.aField, function (field) {
          fixName(field);
          if(field.type === 'date') {
            var match = /^[0-3]?[0-9].[0-3]?[0-9].(?:[0-9]{2})?[0-9]{2}$/.test(field.props.value);
            if(!match) {
              var onlyDate = field.props.value.split('T')[0];
              var splitDate = onlyDate.split('-');
              field.props.value = splitDate[2] + '/' + splitDate[1] + '/' + splitDate[0]
            }
          }
          if(field.type === 'enum') {
            angular.forEach(field.a, function (item) {
              if(field.value === item.id){
                field.value = item.name;
              }
            })
          }
        })
      });
    };

    $scope.addRow = function (form, id, index) {
      ValidationService.validateByMarkers(form, $scope.markers, true, null, true);
      if (!form.$invalid) {
        $scope.tableIsInvalid = false;
        TableService.addRow(id, $scope.creatingTask.aFormProperty);
      } else {
        $scope.tableIsInvalid = true;
        $scope.invalidTableNum = index;
      }
    };

    $scope.removeRow = function (index, form, id) {
      TableService.removeRow($scope.creatingTask.aFormProperty, index, id);
      if (!form.$invalid) {
        $scope.tableIsInvalid = false;
      }
    };

    $scope.rowLengthCheckLimit = function (table) {
      if(table.aRow) return table.aRow.length >= table.nRowsLimit
    };

    $scope.tableIsLoaded = function (item) {
      return typeof item.aRow[0] !== 'number';
    };

    $scope.isFieldWritable = function (field) {
      return TableService.isFieldWritable(field);
    };

    $scope.isVisible = function (field) {
      return TableService.isVisible(field);
    };

    $scope.showField = function () {
      return true
    };

    $scope.backAndForth = function () {
      window.history.back()
    };

    $scope.$watch(function() {
      return localStorage.getItem('creating');
    }, function (newVal, oldVal){
      if(newVal !== oldVal){
        $scope.creatingTask = JSON.parse(localStorage.getItem('creating'));
      }
    });

}]);
