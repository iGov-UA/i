(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('TaskViewCtrl', [
      '$scope', '$stateParams', 'taskData', 'oTask', 'PrintTemplateService', 'iGovMarkers', 'tasks', 'user',
      'taskForm', 'iGovNavbarHelper', 'Modal', 'Auth', 'defaultSearchHandlerService',
      '$state', 'stateModel', 'ValidationService', 'FieldMotionService', 'FieldAttributesService', '$rootScope',
      'lunaService', 'TableService', 'autocompletesDataFactory',
      function ($scope, $stateParams, taskData, oTask, PrintTemplateService, iGovMarkers, tasks, user,
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
        function getRegexContains(str, splitBy, part) {
          var as = str.split(splitBy);
          for (var i = 0; i < as.length; i++) {
            if (as[i].includes(part)) {
              return as[i];
            }
          }
          return null;
        }

        var sLoginAsignee = "sLoginAsignee";

        function getObjFromTaskFormById(id) {
          if(id == null) return null;
          for (var i = 0; i < taskForm.length; i++) {
            if (taskForm[i].id.includes(id)) {
              return taskForm[i];
            }
          }
          return null;
        }

        function convertUsersToEnum(aoUser) {
          var aoNewUser = new Array(aoUser.length);
          for (var i = 0; i < aoUser.length; i++) {
            var item = aoUser[i];
            var newItem = {};
            newItem.id = item.sLogin;
            newItem.name = item.sLastName.trim() + ' ' + item.sFirstName.trim();
            aoNewUser[i] = newItem;
          }
          return aoNewUser;
        }

        function getIdFromActivityProperty(param) {
          if(param == null) return null;
          var item = getObjFromTaskFormById(sLoginAsignee);
          if (item !== null) {
            var as = getRegexContains(item.name, ';', param);
            as = getRegexContains(as, ',', param);
            var sID = as.split('=')[1];
            return sID;
          }
          return null;
        }

        $scope.updateAssigneeName = function(item){
            if (item.id.includes(sLoginAsignee)) {
              for(var i = 0; i < item.enumValues.length;i++) {
                if (item.value == item.enumValues[i].id) {
                  var sAssigneeName= getObjFromTaskFormById(getIdFromActivityProperty("sDestinationFieldID_sName"));
                  if (sAssigneeName != null) {
                    sAssigneeName.value = item.enumValues[i].name;
                    break;
                  }
                }
              }
            }
          }

        fillingUsers();

        function fillingUsers() {
          if (taskData.sLoginAssigned != null) {
            var itemWith_sID = getObjFromTaskFormById(getIdFromActivityProperty("sSourceFieldID_sID_Group"));

            if (itemWith_sID !== null) {
              var group = itemWith_sID.value;
              if (group !== null) {
                var item = getObjFromTaskFormById(sLoginAsignee);
                item.type = "enum";
                user.getUsers(group).then(function (users) {
                  if (users) {
                    sortUsersByAlphabet(users);
                    item.enumValues = convertUsersToEnum(users);
                    if(item.value == null){
                      item.value = item.enumValues[0].id;
                      $scope.updateAssigneeName(item);
                    }
                    // hidden sAssignName
                    hiddenObjById(getIdFromActivityProperty("sDestinationFieldID_sName"));
                  }
                });
              }
            }
          }
        }
        function print(t) {
          console.log(t);
        }



        function sortUsersByAlphabet(items) {
          items.sort(function (a, b) {
            if (a.sLastName > b.sLastName) {
              return 1;
            }
            if (a.sLastName < b.sLastName) {
              return -1;
            }
            if (a.sFirstName > b.sFirstName) {
              return 1;
            }
            if (a.sFirstName< b.sFirstName) {
              return -1;
            }
            return 0;
          });
        }


        //hidden IdGroupNext
        hiddenObjById(getIdFromActivityProperty("sSourceFieldID_sID_Group"));

        function hiddenObjById(id) {
          var itemWith_sID = getObjFromTaskFormById(id);
          if (itemWith_sID !== null && itemWith_sID.readable) {
            itemWith_sID.readable = false;
          }
        }

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

        function searchSelectSubject() {
          angular.forEach(taskForm, function (item) {
            var isExecutorSelect = item.name.split(';')[2];
            if (item.type === 'select' || item.type === 'string' || isExecutorSelect && isExecutorSelect.indexOf('sID_SubjectRole=Executor') > -1) {
              var match;
              if (((match = item.id ? item.id.match(/^s(Currency|ObjectCustoms|SubjectOrganJoinTax|ObjectEarthTarget|Country|ID_SubjectActionKVED|ID_ObjectPlace_UA)(_(\d+))?/) : false))
                ||(item.type == 'select' && (match = item.id ? item.id.match(/^s(Country)(_(\d+))?/) : false)) || isExecutorSelect) {
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
          })
        }
        searchSelectSubject();

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


        function getIdByName(item, asName) {
          var asId = new Array();
          for(var i = 0;i<asName.length;i++){
            asId.push(item[asName[i]]);
          }
          return asId;
        }

        function getValueById(id) {
          for(var i = 0; i < taskForm.length;i++) {
            var item = taskForm[i];
            if (item.id.includes(id)) {
              return item.value;
            }
          }
          return null;
        }

        function getAllNamesFields (item){
          if (item == null) return null;

          var variables = "";
          for (var name in item) {
              variables += name + ",";
          }
          var as = variables.split(",");
          var result = new Array();

          for(var i = 0; i < as.length;i++) {
            if (as[i] != "") {
              result.push(as[i]);
            }
          }

          return result;
        }

        function getVariablesValue(asId){
          if (asId == null) return null;
          var asVariablesValue = new Array(asId.length);
          for(var i = 0; i < asId.length; i++) {
            var result = getValueById(asId[i]);
            if (result == null) {
              Modal.inform.error()('Обєкт з id: ' + asId[i] + ' не має значення. Формула не запрацює.<br> Зніться будь-ласка у технічну підтримку.');
              console.warn('Виникла помилка. Обєкт з id: ' + asId[i] + ' має значення null. Зніться будь-ласка у технічну підтримку.');
              throw 'Виникла помилка. Обєкт з id: ' + asId[i] + ' має значення null. Зніться будь-ласка у технічну підтримку.';
            } else if (!isNaN(result)) {
              asVariablesValue[i] = parseInt(result);
            } else {
              asVariablesValue[i] = result;
            }
          }
          return asVariablesValue;
        }

        function executeFormula(item) {
          var sFormula  = item['sFormula'];
          var sResultName = item['sID_Field_Target'];
          var asVariablesName = getAllNamesFields(item['asID_Field_Alias']);
          var asVariablesId = getIdByName(item['asID_Field_Alias'], asVariablesName);
          var asVariablesValue = getVariablesValue(asVariablesId);

          function getVal(index) {
            return asVariablesValue[index];
          }

          for(var i=0; i < asVariablesName.length; i++) {
              sFormula = sFormula.replace(asVariablesName[i], "getVal(" + i + ")");
          }

          $scope[sResultName] = eval(sFormula);
          console.log($scope[sResultName]);
          console.log(eval(sFormula));
        }



        function runCalculation() {
          var item = getObjFromTaskFormById("marker");
          if (item !== null) {
            var oMotion = JSON.parse(item.value)['motion']; // Generate obj from json(item.value)
            var asNameField = getAllNamesFields(oMotion); //Generate array fields name

            for (var i = 0; i < asNameField.length; i++) {
              if(asNameField[i].includes("PrintFormFormula")) {
                executeFormula(oMotion[asNameField[i]]);
              }
            }
          }
        }

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
            tasks.submitTaskForm($scope.selectedTask.id, $scope.taskForm, $scope.selectedTask, $scope.taskData.aAttachment)
              .then(function (result) {
                if(result.status == 500){
                  var message = result.data.message;
                  var errMsg = (message.includes("errMsg")) ? message.split(":")[1].split("=")[1] : message;
                  $scope.taskForm.isInProcess = false;
                  $scope.convertDisabledEnumFiedsToReadonlySimpleText();
                  Modal.inform.error(function (result) {
                  })(errMsg + " " + (result && result.length > 0 ? (': ' + result) : ''));
                } else {
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
                }
              })
              .catch(defaultErrorHandler);
          }
        };

     $scope.submitTaskQuestion = function (form) {
        Modal.inform.submitTaskQuestion(function() {return $scope.submitTask(form);});
     };

      $scope.println = function (form) {
        console.log("println");
        console.log(form);
        return true;
      }
      $scope.saveChangesTask = function (form) {
          if ($scope.selectedTask && $scope.taskForm) {
            console.log($scope.taskForm);
            $scope.taskForm.isSubmitted = true;

            $scope.taskForm.isInProcess = true;

            rollbackReadonlyEnumFields();
            tasks.saveChangesTaskForm($scope.selectedTask.id, $scope.taskForm, $scope.selectedTask)
              .then(function (result) {
                $scope.taskForm.isInProcess = false;
                if(result.status == 500 || result.status == 403){
                  var message = result.data.message;
                  var errMsg = (message.includes("errMsg")) ? message.split(":")[1].split("=")[1] : message;

                  $scope.convertDisabledEnumFiedsToReadonlySimpleText();

                  Modal.inform.error(function (result) {})(errMsg + " " + (result && result.length > 0 ? (': ' + result) : ''));
                } else {
                  var sMessage = "Форму збережено.";
                  $scope.convertDisabledEnumFiedsToReadonlySimpleText();
                  Modal.inform.success(function (result) {})(sMessage + " " + (result && result.length > 0 ? (': ' + result) : ''));
                }
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

        //Asignee user.
        $scope.choiceUser = function(login) {
          for (var i = 0; i < taskData.aField.length; i++) {
            if (taskData.aField[i].sID.includes(sLoginAsignee)) {
              taskData.aField[i].sValue = login;
              break;
            }
          }
        };

        $scope.inUnassigned = function () {
          return $stateParams.type === "unassigned";
        };

        $scope.tabHistoryAppealChange = function (param) {
          $scope.tabHistoryAppeal = param;
        };

        $scope.newPrint = function (form, id) {
          runCalculation(form);
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

        // проверяем имя поля на наличие заметок
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
        idMatch();

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

        $scope.updateTemplateList = function () {
          $scope.printTemplateList = PrintTemplateService.getTemplates($scope.taskForm);
          if ($scope.printTemplateList.length > 0) {
            $scope.model.printTemplate = $scope.printTemplateList[0];
          }
          return true;
        }

        $scope.tableIsLoaded = function (item) {
          return typeof item.aRow[0] !== 'number';
        };

        $scope.isVisible = function (field) {
          return TableService.isVisible(field);
        };
      }

    ])
})();
