(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('TaskViewCtrl', [
      '$scope', '$stateParams', 'taskData', 'oTask', 'PrintTemplateService', 'iGovMarkers', 'tasks',
      'taskForm', 'iGovNavbarHelper', 'Modal', 'Auth', 'defaultSearchHandlerService',
      '$state', 'stateModel', 'ValidationService', 'FieldMotionService', 'FieldAttributesService', '$rootScope',
      'lunaService', 'TableService', 'autocompletesDataFactory',
      function ($scope, $stateParams, taskData, oTask, PrintTemplateService, iGovMarkers, tasks,
                taskForm, iGovNavbarHelper, Modal, Auth, defaultSearchHandlerService,
                $state, stateModel, ValidationService, FieldMotionService, FieldAttributesService, $rootScope,
                lunaService, TableService, autocompletesDataFactory) {
        var defaultErrorHandler = function (response, msgMapping) {
          defaultSearchHandlerService.handleError(response, msgMapping);
          if ($scope.taskForm) {
            $scope.taskForm.isSuccessfullySubmitted = false;
            $scope.taskForm.isInProcess = false;
          }
        };

        activate();

        function activate(){
          angular.forEach(taskForm, function(item){
            var checkbox = getCheckbox((item.name || '').split(';')[2]);

            if(checkbox){
              bindEnumToCheckbox({
                id: item.id,
                enumValues: item.enumValues,
                sID_CheckboxTrue: checkbox.sID_CheckboxTrue,
                self: item
              });
            }

            if(checkbox && item.type === 'enum'){
              item.type = 'checkbox';
            }
          });


          function getCheckbox(param){
            if(!param || !typeof param === 'string') return null;

            var input = param.trim(),
                finalArray,
                result = {};

            var checkboxExp = input.split(',').filter(function(item){
              return (item && typeof item === 'string' ? item.trim() : '')
                      .split('=')[0]
                      .trim() === 'sID_CheckboxTrue';
            })[0];

            if(!checkboxExp) return null;

            finalArray = checkboxExp.split('=');

            if(!finalArray || !finalArray[1]) return null;

            var indexes = finalArray[1].trim().match(/\d+/ig),
                index;

            if(Array.isArray(indexes)){
              index = isNaN(+indexes[0]) || +indexes[0];;
            }

            result[finalArray[0].trim()] = index !== undefined
            && index !== null
            || index === 0 ? index : finalArray[1].trim();

            return result;
          }

          function bindEnumToCheckbox(param){
            if(!param || !param.id || !param.enumValues ||
                param.sID_CheckboxTrue === null ||
                param.sID_CheckboxTrue === undefined) return;

            var checkbox = {},
                trueValues,
                falseValues;

            if(isNaN(+param.sID_CheckboxTrue)){
              trueValues = param.enumValues.filter(function(o){return o.id === param.sID_CheckboxTrue});
              falseValues = param.enumValues.filter(function(o){return o.id !== param.sID_CheckboxTrue});
              checkbox[param.id] = {
                trueValue: trueValues[0] ? trueValues[0].id : null,
                falseValue: falseValues[0] ? falseValues[0].id : null
              };
            }else{
              falseValues = param.enumValues.filter(function(o, i){return i !== param.sID_CheckboxTrue});
              checkbox[param.id] = {
                trueValue: param.enumValues[param.sID_CheckboxTrue] ?
                    param.enumValues[param.sID_CheckboxTrue].id : null,
                falseValue: falseValues[0] ? falseValues[0].id : null
              };
            }

            angular.extend(param.self, {
                checkbox: checkbox
            });
          }
        }

        $scope.isShowExtendedLink = function () {
          return tasks.isFullProfileAvailableForCurrentUser(taskData);
        };

        $scope.taskData = taskData;
        $scope.printTemplateList = [];
        $scope.model = stateModel;
        $scope.model.printTemplate = null;
        $scope.tableContentShow = false;
        $scope.date = {
          options: {
            timePicker:false
          }
        };

        $scope.taskForm = null;
        $scope.error = null;
        $scope.clarify = false;
        $scope.clarifyFields = {};
        $scope.sSelectedTask = $stateParams.type;
        $scope.selectedTask = oTask;
        $scope.taskId = oTask.id;
        $scope.tabHistoryAppeal = 'appeal';
        $scope.nID_Process = oTask.processInstanceId;
        $scope.markers = ValidationService.getValidationMarkers();
        $scope.bHasEmail = false;
        $scope.isClarifySending = false;
        $scope.tableIsInvalid = false;

        $scope.validateForm = function(form) {
          var bValid = true;
          var oValidationFormData = {};
          angular.forEach($scope.taskForm, function (field) {
            oValidationFormData[field.id] = angular.copy(field);
            if(field.type === 'file'){
              //debugger;
            }
          });
          ValidationService.validateByMarkers(form, null, true, oValidationFormData);
          return form.$valid && bValid;
        };

        var addIndexForFileItems = function (val) {
          var idx = 0;
          return (val || []).map(function (item) {
            if (item.type === 'file') {
              item.nFileIdx = idx;
              idx++;
            }
            return item;
          });
        };

        var isItemFormPropertyDisabled = function (oItemFormProperty){
          if (!$scope.selectedTask || !$scope.selectedTask.assignee || !oItemFormProperty
            || !$scope.sSelectedTask || $scope.sSelectedTask === 'finished')
          return true;

          var sID_Field = oItemFormProperty.id;
          if (sID_Field === null) {
            return true;
          }
          if (!oItemFormProperty.writable) {
            return true;
          }
          //var bNotBankID =
          var bEditable = sID_Field.indexOf("bankId") !== 0;
          var sFieldName = oItemFormProperty.name;
          if (sFieldName === null) {
            return true;
          }
          var as = sFieldName.split(";");
          if (as.length > 2) {
            bEditable = as[2] === "writable=true" ? true : as[2] === "writable=false" ? false : bEditable;
          }

          return !bEditable;
        };

        $scope.taskForm = addIndexForFileItems(taskForm);

        $scope.printTemplateList = PrintTemplateService.getTemplates($scope.taskForm);
        if ($scope.printTemplateList.length > 0) {
          $scope.model.printTemplate = $scope.printTemplateList[0];
        }
        $scope.taskForm.taskData = taskData;

        if (!oTask.endTime) {
          $scope.taskForm.forEach(function (field) {
            if (field.type === 'markers' && $.trim(field.value)) {
              var sourceObj = null;
              try {
                sourceObj = JSON.parse(field.value);
              } catch (ex) {
                console.log('markers attribute ' + field.name + ' contain bad formatted json\n' + ex.name + ', ' + ex.message + '\nfield.value: ' + field.value);
              }
              if (sourceObj !== null) {
                _.merge(iGovMarkers.getMarkers(), sourceObj, function (destVal, sourceVal) {
                  if (_.isArray(sourceVal)) {
                    return sourceVal;
                  }
                });
              }
            }
          });
        }

        function getAdaptedFormData(taskForm) {
          var oAdaptFormData = {};
          angular.forEach(taskForm, function (item) {
            oAdaptFormData[item.id] = {
              required: item.required,
              value: item.value,
              writable: item.writable
            }
          });
          return oAdaptFormData;
        }

        $scope.isRequired = function (item) {
          var bRequired = FieldMotionService.FieldMentioned.inRequired(item.id) ?
            FieldMotionService.isFieldRequired(item.id, getAdaptedFormData($scope.taskForm)) : item.required;
          var b = !$scope.isFormPropertyDisabled(item) && (bRequired || $scope.isCommentAfterReject(item));
          return b;
        };

        $scope.isTaskSubmitted = function (item) {
          return $scope.taskForm.isSubmitted;
        };

        $scope.isTaskSuccessfullySubmitted = function () {
          if ($scope.selectedTask && $scope.taskForm) {
            if ($scope.taskForm.isSuccessfullySubmitted != undefined && $scope.taskForm.isSuccessfullySubmitted)
              return true;
          }
          return false;
        };

        $scope.isTaskInProcess = function () {
          if ($scope.selectedTask && $scope.taskForm) {
            if ($scope.taskForm.isInProcess != undefined && $scope.taskForm.isInProcess)
              return true;
          }
          return false;
        };

        $scope.clarify = false;

        $scope.clarifyToggle = function () {
          $scope.clarify = !$scope.clarify;
        };

        $scope.clarifyModel = {
          sBody: ''
        };

        $scope.clarifySend = function () {
          $scope.isClarifySending = true;

          var oData = {
            nID_Process: $scope.nID_Process,
            saField: '',
            soParams: '',
            sMail: '',
            sBody: $scope.clarifyModel.sBody
          };

          var soParams = {sEmployerFIO: $scope.getCurrentUserName};
          var aFields = [];
          var sClientFIO = null;
          var sClientName = null;
          var sClientSurname = null;

          angular.forEach($scope.taskForm, function (item) {
            if (angular.isDefined($scope.clarifyFields[item.id]) && $scope.clarifyFields[item.id].clarify)
              aFields.push({
                sID: item.id !== null ? item.id : "",
                sName: $scope.sFieldLabel(item.name) !== null ? $scope.sFieldLabel(item.name) : "",
                sType: item.type !== null ? item.type : "",
                sValue: item.value !== null ? item.value : "",
                sValueNew: item.value !== null ? item.value : "",
                sNotify: $scope.clarifyFields[item.id].text !== null ? $scope.clarifyFields[item.id].text : ""
              });

            if (item.id === 'email') {
              oData.sMail = item.value;
            }
            //<activiti:formProperty id="bankIdfirstName" name="Ім'я" type="string" ></activiti:formProperty>
            //<activiti:formProperty id="bankIdmiddleName" name="По Батькові" type="string" ></activiti:formProperty>
            if (item.id === 'bankIdfirstName') {
              sClientName = item.value;
            }
            if (item.id === 'bankIdmiddleName') {
              sClientSurname = item.value;
            }
          });

          if ($scope.clarifyModel.sBody.trim().length === 0 && aFields.length === 0) {
            Modal.inform.warning()('Треба ввести коментар або обрати поле/ля');
            return;
          }


          if (sClientName !== null) {
            sClientFIO = sClientName;
            if (sClientSurname !== null) {
              sClientFIO += " " + sClientSurname;
            }
          }
          if (sClientFIO !== null) {
            soParams["sClientFIO"] = sClientFIO;
          }

          oData.saField = JSON.stringify(aFields);
          oData.soParams = JSON.stringify(soParams);
          if(oData.saField === "[]") {
            oData.nID_Process = oData.nID_Process + lunaService.getLunaValue(oData.nID_Process);
            tasks.postServiceMessages(oData).then(function () {
              $scope.clarify = false;
              $scope.isClarifySending = false;
              Modal.inform.success(function () {
              })('Коментар відправлено успішно');
            });
          } else {
            tasks.setTaskQuestions(oData).then(function () {
              $scope.clarify = false;
              $scope.isClarifySending = false;
              Modal.inform.success(function () {
              })('Зауваження відправлено успішно');
            });
          }
        };

        (function isTaskHasEmail() {
          try{
            for(var i=0; i<$scope.taskData.aField.length; i++){
              if($scope.taskData.aField[i].sID === "email"){
                $scope.bHasEmail = true;
              }
            }
          } catch (err){
            if($scope.taskData.code && $scope.taskData.message){
              console.warn($scope.taskData.message);
            } else {
              console.error(err);
            }
          }
        })();

        $scope.checkSignState = {inProcess: false, show: false, signInfo: null, attachmentName: null};

        $scope.checkAttachmentSign = function (nID_Task, nID_Attach, attachmentName) {
          $scope.checkSignState.inProcess = true;
          tasks.checkAttachmentSign(nID_Task, nID_Attach).then(function (signInfo) {
            if (signInfo.customer) {
              $scope.checkSignState.show = !$scope.checkSignState.show;
              $scope.checkSignState.signInfo = signInfo;
              $scope.checkSignState.attachmentName = attachmentName;
            } else if (signInfo.code) {
              $scope.checkSignState.show = false;
              $scope.checkSignState.signInfo = null;
              $scope.checkSignState.attachmentName = null;
              Modal.inform.warning()(signInfo.message);
            } else {
              $scope.checkSignState.show = false;
              $scope.checkSignState.signInfo = null;
              $scope.checkSignState.attachmentName = null;
              Modal.inform.warning()('Немає підпису');
            }
          }).catch(function (error) {
            $scope.checkSignState.show = false;
            $scope.checkSignState.signInfo = null;
            $scope.checkSignState.attachmentName = null;
            Modal.inform.error()(error.message);
          }).finally(function () {
            $scope.checkSignState.inProcess = false;
          });
        };

        $scope.isFormPropertyDisabled = isItemFormPropertyDisabled;

        $scope.print = function () {
          if ($scope.selectedTask && $scope.taskForm) {
            rollbackReadonlyEnumFields();
            $scope.printModalState.show = !$scope.printModalState.show;
          }
        };

        $scope.hasUnPopulatedFields = function () {
          if ($scope.selectedTask && $scope.taskForm) {
            var unpopulated = $scope.taskForm.filter(function (item) {
              return (item.value === undefined || item.value === null || item.value.trim() === "") && (item.required || $scope.isCommentAfterReject(item));//&& item.type !== 'file'
            });
            return unpopulated.length > 0;
          } else {
            return true;
          }
        };

        $scope.unpopulatedFields = function () {
          if ($scope.selectedTask && $scope.taskForm) {
            var unpopulated = $scope.taskForm.filter(function (item) {
              return (item.value === undefined || item.value === null || item.value.trim() === "") && (item.required || $scope.isCommentAfterReject(item));//&& item.type !== 'file'
            });
            return unpopulated;
          } else {
            return [];
          }
        };

        $scope.isFormInvalid = false;
        $scope.submitTask = function (form) {
          $scope.validateForm(form);
          if(form.$invalid){
            $scope.isFormInvalid = true;
            return;
          } else {
            $scope.isFormInvalid = false;
          }

          if ($scope.selectedTask && $scope.taskForm) {
            $scope.taskForm.isSubmitted = true;

            var unpopulatedFields = $scope.unpopulatedFields();
            if (unpopulatedFields.length > 0) {
              // var errorMessage = 'Будь ласка, заповніть поля: ';

              // if (unpopulatedFields.length == 1) {
              //
              //   var nameToAdd = unpopulatedFields[0].name;
              //   if (nameToAdd.length > 50) {
              //     nameToAdd = nameToAdd.substr(0, 50) + "...";
              //   }
              //
              //   errorMessage = "Будь ласка, заповніть полe '" + nameToAdd + "'";
              // }
              // else {
              //   unpopulatedFields.forEach(function (field) {
              //
              //     var nameToAdd = field.name;
              //     if (nameToAdd.length > 50) {
              //       nameToAdd = nameToAdd.substr(0, 50) + "...";
              //     }
              //     errorMessage = errorMessage + "'" + nameToAdd + "',<br />";
              //   });
              //   var comaIndex = errorMessage.lastIndexOf(',');
              //   errorMessage = errorMessage.substr(0, comaIndex);
              // }
              // Modal.inform.error()(errorMessage);
              setTimeout(function () {
                angular.element('.submitted').first().focus();
              },100);

              return;
            }

            $scope.taskForm.isInProcess = true;

            rollbackReadonlyEnumFields();
            tasks.submitTaskForm($scope.selectedTask.id, $scope.taskForm, $scope.selectedTask)
              .then(function (result) {
                var sMessage = "Форму відправлено.";
                angular.forEach($scope.taskForm, function (oField) {
                  if (oField.id === "sNotifyEvent_AfterSubmit") {
                    sMessage = oField.value;
                  }
                });
                $scope.convertDisabledEnumFiedsToReadonlySimpleText();


                Modal.inform.success(function (result) {
                  $scope.lightweightRefreshAfterSubmit();
                })(sMessage + " " + (result && result.length > 0 ? (': ' + result) : ''));

                $scope.$emit('task-submitted', $scope.selectedTask);
              })
              .catch(defaultErrorHandler);
          }
        };

        $scope.assignTask = function () {
          rollbackReadonlyEnumFields();
          $scope.taskForm.isInProcess = true;

          tasks.assignTask($scope.selectedTask.id, Auth.getCurrentUser().id)
            .then(function (result) {
              Modal.assignTask(function (event) {
                $state.go('tasks.typeof.view', {type:'selfAssigned'});
              }, 'Задача у вас в роботі', $scope.lightweightRefreshAfterSubmit);

            })
            .catch(defaultErrorHandler);
        };

        $scope.unassign = function () {
          rollbackReadonlyEnumFields();
          tasks.unassign($scope.selectedTask.id)
            .then(function () {
              $scope.selectTask($scope.selectedTask);
            })
            .then(function () {
              return tasks.getTask($scope.selectedTask.id);
            })
            .then(function (updatedTaskResult) {
              angular.copy(updatedTaskResult, $scope.selectedTask);
            })
            .catch(defaultErrorHandler);
        };

        $scope.upload = function (files, propertyID) {
          tasks.upload(files, $scope.taskId).then(function (result) {
            var filterResult = $scope.taskForm.filter(function (property) {
              return property.id === propertyID;
            });
            if (filterResult && filterResult.length === 1) {
              filterResult[0].value = result.response.id;
              filterResult[0].fileName = result.response.name;
              filterResult[0].signInfo = result.signInfo;
            }
          }).catch(function (err) {
            Modal.inform.error()('Помилка. ' + err.code + ' ' + err.message);
          });
        };

        $scope.lightweightRefreshAfterSubmit = function () {
          //lightweight refresh only deletes the submitted task from the array of current type of tasks
          //so we don't need to refresh the whole page
          iGovNavbarHelper.loadTaskCounters();
          $scope.taskForm.isInProcess = false;
          $scope.taskForm.isSuccessfullySubmitted = true;
        };

        $scope.sFieldLabel = function (sField) {
          var s = '';
          if (sField !== null) {
            var a = sField.split(';');
            s = a[0].trim();
          }
          return s;
        };

        $scope.nID_FlowSlotTicket_FieldQueueData = function (sValue) {
          var nID_FlowSlotTicket = 0;
          try {
            var nAt = sValue.indexOf(":");
            var nTo = sValue.indexOf(",");
            nID_FlowSlotTicket = sValue.substring(nAt + 1, nTo);;
          } catch (_) {
            nID_FlowSlotTicket = 1;
          }
          return nID_FlowSlotTicket;
        };

        $scope.sDate_FieldQueueData = function (sValue) {
          var sDate = "Дата назначена!";
          try {
            var nAt = sValue.indexOf("sDate");
            var nTo = sValue.indexOf("}");
            sDate = sValue.substring(nAt + 5 + 1 + 1 + 1, nTo - 1 - 6);
          } catch (_) {
            sDate = "Дата назначена!";
          }
          return sDate;
        };

        $scope.sEnumValue = function (aItem, sID) {
          var s = sID;
          _.forEach(aItem, function (oItem) {
            if (oItem.id == sID) {
              s = oItem.name;
            }
          });
          return s;
        };

        $scope.getMessageFileUrl = function (oMessage, oFile) {
          if(oMessage && oFile)
          return './api/tasks/' + $scope.nID_Process + '/getMessageFile/' + oMessage.nID + '/' + oFile.sFileName;
        };

        $scope.getCurrentUserName = function () {
          var user = Auth.getCurrentUser();
          return user.firstName + ' ' + user.lastName;
        };

        $scope.isCommentAfterReject = function (item) {
          if (item.id != "comment") return false;

          var decision = $.grep($scope.taskForm, function (e) {
            return e.id == "decide";
          });

          if (decision.length == 0) {
            // no decision
          } else if (decision.length == 1) {
            if (decision[0].value == "reject") return true;
          }
          return false;
        };

        // change "enum" field to "string" (https://github.com/e-government-ua/i/issues/751)
        $scope.convertDisabledEnumFiedsToReadonlySimpleText = function () {
          $scope.originalTaskForm = jQuery.extend(true, {}, $scope.taskForm);
          for (var i = 0; i < taskForm.length; i++) {
            if ($scope.originalTaskForm[i].type === "enum" && isItemFormPropertyDisabled($scope.originalTaskForm[i])) {
              $scope.taskForm[i].type = "string";
              for (var j = 0; j < $scope.originalTaskForm[i].enumValues.length; j++) {
                if ($scope.originalTaskForm[i].value === $scope.originalTaskForm[i].enumValues[j].id) {
                  $scope.taskForm[i].value = $scope.originalTaskForm[i].enumValues[j].name;
                }
              }
              try {
                $scope.taskForm.taskData.aField[i].sType = "string";
                var keyCandidate = $scope.originalTaskForm.taskData.aField[i].sValue;
                var objCandidate = $scope.originalTaskForm.taskData.aField[i].mEnum;
                $scope.taskForm.taskData.aField[i].sValue = objCandidate[keyCandidate];
              } catch (e) {
                Modal.inform.error()($scope.taskForm.taskData.message)
              }
            }
          }
        };
        function rollbackReadonlyEnumFields() {
          for (var i = 0; i < taskForm.length; i++) {
            if ($scope.originalTaskForm[i].type === "enum" && isItemFormPropertyDisabled($scope.originalTaskForm[i])) {
              $scope.taskForm[i].type = "enum";
              $scope.taskForm[i].value = $scope.originalTaskForm[i].value;
              try {
                $scope.taskForm.taskData.aField[i].sType = "string";
                $scope.taskForm.taskData.aField[i].sType = $scope.originalTaskForm.taskData.aField[i].sType;
                $scope.taskForm.taskData.aField[i].sValue = $scope.originalTaskForm.taskData.aField[i].sValue;
              } catch (e) {
                Modal.inform.error()($scope.taskForm.taskData.message)
              }
            }
          }
        }
        $scope.convertDisabledEnumFiedsToReadonlySimpleText();

        $scope.isFieldVisible = function(item) {
          return item.id !== 'processName' && (FieldMotionService.FieldMentioned.inShow(item.id) ?
              FieldMotionService.isFieldVisible(item.id, $scope.taskForm) : true);
        };

        $scope.creationDateFormatted = function (date) {
          if (date){
            var unformatted = date.split(' ')[0];
            var splittedDate = unformatted.split('-');
            return splittedDate[2] + '.' + splittedDate[1] + '.' + splittedDate[0];
          }
        };

        $scope.inUnassigned = function () {
          return $stateParams.type === "unassigned";
        };

        $scope.tabHistoryAppealChange = function (param) {
          $scope.tabHistoryAppeal = param;
        };

        $scope.newPrint = function (form, id) {
          $scope.model.printTemplate = id;
          $scope.print(form);
        };

        $scope.isClarify = function (name) {
          return name.indexOf('writable=false') !== -1 ;
        };

        var activeFieldsList = [];
        angular.forEach($scope.taskForm, function (item) {
          if($scope.isFieldVisible(item)
            && !$scope.isFormPropertyDisabled(item)
            && item.type !== 'invisible'
            && item.type !== 'label'
            && item.type !== 'markers') {
            activeFieldsList.push(item);
          }
        });

        $scope.insertSeparator = function(sPropertyId){
          return FieldAttributesService.insertSeparators(sPropertyId);
        };

        $scope.isTableAttachment = function (item) {
          return item.indexOf('[table]') > -1;
        };

        $scope.isUnDisabledFields = function () {
          return activeFieldsList.length > 0;
        };

        $scope.openTableAttachment = function (id, taskId) {
          $scope.attachIsLoading = true;

          tasks.getTableAttachment(taskId, id).then(function (res) {
            $scope.openedAttachTable = JSON.parse(res);
            fixFieldsForTable($scope.openedAttachTable);
            $scope.attachIsLoading = false;
          });

          $scope.tableContentShow = !$scope.tableContentShow;
        };

        var fixFieldsForTable = function (table) {
            var tableRow;
            if('content' in table){
              tableRow = table.content;
            } else {
              tableRow = table.aRow;
            }
            angular.forEach(tableRow, function (row) {
              angular.forEach(row.aField, function (field) {
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

        var idMatch = function () {
          angular.forEach($scope.taskForm, function (item, key, obj) {
            angular.forEach($scope.taskData.aAttachment, function (attachment) {
              var reg = /(\[id=(\w+)\])/;
              var match = attachment.description.match(reg);
              if(match !== null && (item.id && match[2].toLowerCase() === item.id.toLowerCase() ||item.name && match[2].toLowerCase() === item.name.toLowerCase())) {
                tasks.getTableAttachment(attachment.taskId, attachment.id).then(function (res) {
                  obj[key] = JSON.parse(res);
                  obj[key].description = attachment.description;
                })
              }
            })
          });
        };
        // idMatch();

        TableService.init($scope.taskForm);

        $scope.addRow = function (form, id, index) {
          ValidationService.validateByMarkers(form, null, true, null, true);
          if (!form.$invalid) {
            $scope.tableIsInvalid = false;
            TableService.addRow(id, $scope.taskForm);
          } else {
            $scope.tableIsInvalid = true;
            $scope.invalidTableNum = index;
          }
        };

        $scope.removeRow = function (index, form, id) {
          TableService.removeRow($scope.taskForm, index, id);
          if (!form.$invalid) {
            $scope.tableIsInvalid = false;
          }
        };
        $scope.rowLengthCheckLimit = function (table) {
          if(table.aRow) return table.aRow.length >= table.nRowsLimit
        };

        $scope.isFieldWritable = function (field) {
          return TableService.isFieldWritable(field);
        };

        $scope.tableIsLoaded = function (item) {
          return typeof item.aRow[0] !== 'number';
        };
      }

    ])
})();
