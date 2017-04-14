(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('TaskViewCtrl', [
      '$scope', '$stateParams', 'taskData', 'oTask', 'PrintTemplateService', 'iGovMarkers', 'tasks', 'user',
      'taskForm', 'iGovNavbarHelper', 'Modal', 'Auth', 'defaultSearchHandlerService',
      '$state', 'stateModel', 'ValidationService', 'FieldMotionService', 'FieldAttributesService', '$rootScope',
      'lunaService', 'TableService', 'autocompletesDataFactory', 'documentRights', 'documentLogins', '$filter',
      'processSubject', '$sce', 'eaTreeViewFactory',
      function ($scope, $stateParams, taskData, oTask, PrintTemplateService, iGovMarkers, tasks, user,
                taskForm, iGovNavbarHelper, Modal, Auth, defaultSearchHandlerService,
                $state, stateModel, ValidationService, FieldMotionService, FieldAttributesService, $rootScope,
                lunaService, TableService, autocompletesDataFactory, documentRights, documentLogins, $filter,
                processSubject, $sce, eaTreeViewFactory) {
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
            if (as[i].indexOf(part) >= 0) {
              return as[i];
            }
          }
          return null;
        }

        FieldMotionService.reset();
        iGovMarkers.reset();
        iGovMarkers.init();

        var sLoginAsignee = "sLoginAsignee";

        function getObjFromTaskFormById(id) {
          if(id == null) return null;
          for (var i = 0; i < taskForm.length; i++) {
//             if (taskForm[i].id && taskForm[i].id.includes && taskForm[i].id.includes(id)) {
//               return taskForm[i];
//             }
            if (taskForm[i].id && taskForm[i].id.indexOf(id) >= 0) {
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
        };

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

        if(documentRights) {
          $scope.documentRights = documentRights;
          if(documentLogins) $scope.documentLogins = documentLogins;
        }

        if(processSubject) {
          $scope.aProcessSubject = processSubject.aProcessSubject;
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
              index = isNaN(+indexes[0]) || +indexes[0];
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
            var isExecutorSelect = item.name ? item.name.split(';')[2] : null;
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
        $scope.markers = iGovMarkers.getMarkers();
        $scope.bHasEmail = false;
        $scope.isClarifySending = false;
        $scope.tableIsInvalid = false;
        $scope.taskData.aTable = [];
        $scope.usersHierarchyOpened = false;
        $scope.taskData.aNewAttachment = [];
        $rootScope.delegateSelectMenu = false;

        // todo соеденить с isUnasigned
        $scope.isDocument = function () {
          return $state.params.type === 'documents';
        };

        $scope.validateForm = function(form) {
          var bValid = true;
          var oValidationFormData = {};
          angular.forEach($scope.taskForm, function (field) {
            oValidationFormData[field.id] = angular.copy(field);
            if(field.type === 'file'){
              //debugger;
            }
          });
          ValidationService.validateByMarkers(form, $scope.markers, true, oValidationFormData);
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
          if (!$scope.selectedTask || (!$scope.selectedTask.assignee && !$scope.isDocument()) || !oItemFormProperty
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

        $scope.printTemplateList = {};

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
                _.merge($scope.markers, sourceObj, function (destVal, sourceVal) {
                  if (_.isArray(sourceVal)) {
                    return sourceVal;
                  }
                });
              }
            }
          });
        }

        function downloadFileHTMLContent() {
          angular.forEach($scope.taskForm, function (i, k, o) {
            if(i.type === 'fileHTML' && i.value && i.value.indexOf('sKey') > -1) {
              tasks.getTableOrFileAttachment($scope.taskData.oProcess.nID, i.id, true).then(function (res) {
                o[k].valueVisible = res;
              })
            }
          })
        }
        downloadFileHTMLContent();

        extractFieldOption($scope.taskForm);

        function extractFieldOption(aProperties) {
          angular.forEach(aProperties, function (property) {
            var i, source, equalsIndex, key, val;
            if(!property.options) property.options = {};

            if(property.name && property.name.indexOf(';;') >= 0){
              var as = property.name.split(';;');
              property.name = as[0];
              for(i = 1; i < as.length; i++){
                source = as[i];
                equalsIndex = source.indexOf('=');
                key = source.substr(0, equalsIndex).trim();
                try {
                  val = angular.fromJson(source.substr(equalsIndex + 1).trim())
                } catch (e){
                  val = source.substr(equalsIndex + 1).trim();
                }
                property.options[key] = val;
              }
            }

            if(property.name && property.name.indexOf(';') >= 0){
              var sOldOptions = property.name.split(';')[2];
              if(sOldOptions){
                var aOptions = sOldOptions.split(',');
                for(i = 0; i < aOptions.length; i++){
                  source = aOptions[i];
                  equalsIndex = source.indexOf('=');
                  key = source.substr(0, equalsIndex).trim();
                  try {
                    val = angular.fromJson(source.substr(equalsIndex + 1).trim())
                  } catch (e){
                    val = source.substr(equalsIndex + 1).trim();
                  }
                  property.options[key] = val;
                }
              }
            }
          })
        }

        function fillArrayWithNewAttaches() {
          angular.forEach($scope.taskForm, function (item) {
            if(item.type === 'file' || item.type === 'table' || item.type === 'string') {
              try {
                var parsedValue = JSON.parse(item.value);
                if(parsedValue && parsedValue.sKey) {
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
                  $scope.taskData.aNewAttachment.push(item);
                }
              }catch(e){}
            }
          })
        }fillArrayWithNewAttaches();

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

        $scope.correctSignName = function (name) {
          var splitName = name.split(';');
          return splitName.length !== 1 ? splitName[0] : name;
        };

        $scope.takeTheKeyFromJSON = function (item) {
          return JSON.parse(item.value).sKey;
        };

        $scope.takeTheFileNameFromJSON = function (item) {
          var originalFileName = JSON.parse(item.value).sFileNameAndExt;
          var ext;
          if (originalFileName && originalFileName.indexOf('.') > 0){
            var parts = originalFileName.split(".");
            ext = parts[parts.length - 1];
          }
          if(ext){
            return item.name + '.' + ext;
          }
          return item.name;
        };

        $scope.takeTheKeyFromJSON = function (item) {
          return JSON.parse(item.value).sKey;
        };

        $scope.takeTheFileNameFromJSON = function (item) {
          var originalFileName = JSON.parse(item.value).sFileNameAndExt;
          var ext;
          if (originalFileName && originalFileName.indexOf('.') > 0){
            var parts = originalFileName.split(".");
            ext = parts[parts.length - 1];
          }
          if(ext){
            return item.name + '.' + ext;
          }
          return item.name;
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

        /*
        * проверка наличия эцп. поддерживается старый и новый сервис, разделение по 4му параметру
        * @param nID_Task - ид таски (если новый серивс - ид процесса);
        * @param nID_Attach - ид аттача;
        * @param attachmentName - для старого сервиса передаеться sDescription, для нового - name;
        * @param {boolean} isNewAttachment - если true используеться новый сервис checkProcessAttach, иначе check_attachment_sign
        */
        $scope.checkAttachmentSign = function (nID_Task, nID_Attach, attachmentName, isNewAttach) {
          $scope.checkSignState.inProcess = true;
          tasks.checkAttachmentSign(nID_Task, nID_Attach, isNewAttach).then(function (signInfo) {
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
            //Modal.inform.error()(error.message);
          }).finally(function () {
            $scope.checkSignState.inProcess = false;
          });
        };

        $scope.isFormPropertyDisabled = isItemFormPropertyDisabled;

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
            if (item.id.indexOf(id) >= 0) {
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
            var item = getObjFromTaskFormById(asId[i]), value, message;
            if(!item) {
              message = 'Зверніться у технічну підтримку. Обєкт з id ' + asId[i] + ' відсутній. Формула не запрацює.';
              Modal.inform.error()(message);
              throw message;
            }

            if (!(value = item.value)) {
              return undefined;
              // message = 'Пусте поле ' + item.name + '. Прінт Формула не запрацює.';
              // Modal.inform.error()(message);
              // throw message;
            } else if (!isNaN(value)) {
              asVariablesValue[i] = parseInt(value);
            } else {
              asVariablesValue[i] = value;
            }
          }
          return asVariablesValue;
        }

        function pushResultFormula(id, value) {
          var item = getObjFromTaskFormById(id);
          if (item != null) item.value = value;
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

          if (asVariablesValue === undefined) {
            pushResultFormula(sResultName, null);
            return;
          }
          String.prototype.replaceAll = function(search, replacement) {
            var target = this;
            return target.replace(new RegExp(search, 'g'), replacement);
          };

          for(var i=0; i < asVariablesName.length; i++) {
            sFormula = sFormula.replaceAll(asVariablesName[i], "getVal(" + i + ")");
          }
          pushResultFormula(sResultName, eval(sFormula));
        }



        function runCalculation() {
          var item = getObjFromTaskFormById("marker");
          if (item !== null) {
            var oMotion = JSON.parse(item.value)['motion']; // Generate obj from json(item.value)
            var asNameField = getAllNamesFields(oMotion); //Generate array fields name

            /*todo иногда oMotion возвращает undefined, что в итоге делает asNameField - null,
             *в итоге ломаеться принтформа
             */
            if(asNameField){
              for (var i = 0; i < asNameField.length; i++) {
                if(asNameField[i].indexOf("PrintFormFormula") >= 0) {
                  executeFormula(oMotion[asNameField[i]]);
                }
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
              return (item.value === null && !item.valueVisible) && (item.value === undefined || item.value === null || item.value.trim() === "") && (item.required || $scope.isCommentAfterReject(item));//&& item.type !== 'file'
            });
            return unpopulated;
          } else {
            return [];
          }
        };

        $scope.isFormInvalid = false;
        $scope.submitTask = function (form, bNotShowSuccessModal) {
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
            if(documentRights) {
              angular.forEach($scope.taskForm, function (item, key, obj) {
                if(item.type === 'date') {
                  obj[key].value = $filter('checkDate')(item.value);
                }
              });
              var documentUnpopulatedFields = unpopulatedFields.filter(function (field) {
                return field.id.indexOf(documentRights.asID_Field_Write) === -1
              })
            }
            if ((!documentUnpopulatedFields && unpopulatedFields.length > 0)
                || (documentUnpopulatedFields && documentUnpopulatedFields.length > 0)) {
              var errorMessage = 'Будь ласка, заповніть поля: ';

              if (unpopulatedFields.length == 1) {

                var nameToAdd = unpopulatedFields[0].name;
                var idToAdd = unpopulatedFields[0].id;
                if (nameToAdd.length > 50) {
                  nameToAdd = nameToAdd.substr(0, 50) + "...";
                }

                errorMessage = "Будь ласка, заповніть полe '" + nameToAdd + "' (id поля: '" + idToAdd + "')";
              }
              else {
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
              Modal.inform.error()(errorMessage);
              setTimeout(function () {
                angular.element('.submitted').first().focus();
              },100);

              return;
            }

            $scope.taskForm.isInProcess = true;

            rollbackReadonlyEnumFields();
            if($scope.model.printTemplate){
              $scope.taskForm.sendDefaultPrintForm = false;
            }
            if($scope.taskData.oProcess && $scope.taskData.oProcess.sBP && $scope.taskData.oProcess.sBP.match(/^_doc_/)){
              var sKey_Step_field = $scope.taskForm.filter(function (item) {
                return item.id === "sKey_Step_Document";
              })[0];
              if(sKey_Step_field){
                $scope.taskForm.sendDefaultPrintForm = !!sKey_Step_field.value;
              }
            }
            tasks.submitTaskForm($scope.selectedTask.id, $scope.taskForm, $scope.selectedTask, $scope.taskData.aAttachment)
              .then(function (result) {
                if(result.status == 500){
                  var message = result.data.message;
                  var errMsg = (message.indexOf("errMsg") >= 0) ? message.split(":")[1].split("=")[1] : message;
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

                  if(!bNotShowSuccessModal && iGovNavbarHelper.currentTab.indexOf("documents") >= 0){
                    bNotShowSuccessModal = true;
                  }

                  if(bNotShowSuccessModal){
                    $scope.lightweightRefreshAfterSubmit();
                  } else {
                    Modal.inform.success(function (result) {
                      $scope.lightweightRefreshAfterSubmit();
                    })(sMessage + " " + (result && result.length > 0 ? (': ' + result) : ''));
                  }

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
            tasks.saveChangesTaskForm($scope.selectedTask.id, $scope.taskForm, $scope.selectedTask,  $scope.taskData.aAttachment)
              .then(function (result) {
                $scope.taskForm.isInProcess = false;
                if(result.status == 500 || result.status == 403){
                  var message = result.data.message;
                  var errMsg = (message.indexOf("errMsg") >= 0) ? message.split(":")[1].split("=")[1] : message;

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
          $rootScope.switchProcessUploadingState();
          var isNewAttachmentService = false;
          var taskID = $scope.taskId;
          for(var i=0; i<$scope.taskForm.length; i++) {
            var item = $scope.taskForm[i];
            var splitNameForOptions = item.name.split(';');
            if(item.type !== 'table' && item.id === propertyID && splitNameForOptions.length === 3){
              if(splitNameForOptions[2].indexOf('bNew=true') !== -1) {
                isNewAttachmentService = true;
                taskID = $scope.taskData.oProcess.nID;
                break
              }
            } else if(item.type === 'table') {
              if(item.aRow.length !== 0) {
                for(var t=0; t<item.aRow.length; t++) {
                  var row = item.aRow[t];
                  for(var f=0; f<row.aField.length; f++) {
                    var field = row.aField[f];
                    var fieldOptions = field.name.split(';');
                    if(field.id === propertyID && fieldOptions.length === 3){
                      if(fieldOptions[2].indexOf('bNew=true') !== -1) {
                        isNewAttachmentService = true;
                        taskID = $scope.taskData.oProcess.nID;
                        break
                      }
                    }
                  }
                }
              }
            }
          }
          tasks.upload(files, taskID, propertyID, isNewAttachmentService).then(function (result) {
            var filterResult = $scope.taskForm.filter(function (property) {
              return property.id === propertyID;
            });

            // if filterResult === 0 => check file in table
            if(filterResult.length === 0) {
              for(var j=0; j<$scope.taskForm.length; j++) {
                if($scope.taskForm[j].type === 'table') {
                  for(var c=0; c<$scope.taskForm[j].aRow.length; c++) {
                    var row = $scope.taskForm[j].aRow[c];
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
            //Modal.inform.error()('Помилка. ' + err.code + ' ' + err.message);
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
          if (sField) {
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

        $scope.getCurrentUserLogin = function () {
          var user = Auth.getCurrentUser();
          return user.id;
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
              for (var j = 0; j < $scope.originalTaskForm[i].enumValues.length; j++) {
                if ($scope.originalTaskForm[i].value === $scope.originalTaskForm[i].enumValues[j].id) {
                  $scope.taskForm[i].value = $scope.originalTaskForm[i].enumValues[j].name;
                }
              }
              try {
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
              $scope.taskForm[i].value = $scope.originalTaskForm[i].value;
              try {
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
          var bVisible = item.id !== 'processName' && (FieldMotionService.FieldMentioned.inShow(item.id) ?
              FieldMotionService.isFieldVisible(item.id, $scope.taskForm) : true);
          if(item.options && item.options.hasOwnProperty('bVisible')){
            bVisible = bVisible && item.options['bVisible'];
          }
          return bVisible;
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
            if (taskData.aField[i].sID.indexOf(sLoginAsignee) >= 0) {
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
          $scope.print(form, true);
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

        $scope.insertOrdersSeparator = function(sPropertyId){
          var oLine = FieldAttributesService.insertSeparators(sPropertyId);
          var oItem = null;
          if (oLine.bShow){
            angular.forEach($scope.taskForm, function (item) {
              if (item.id == oLine.sLinkedFieldID) oItem = item;
            });
            if(oItem){
              oLine.bShow = oItem.value && $scope.isFormPropertyDisabled(oItem);
            } else {
              oLine.bShow = false;
            }
          }
          return oLine;
        };

        $scope.insertSeparator = function(sPropertyId){
          return FieldAttributesService.insertSeparators(sPropertyId);
        };

        $scope.isTableAttachment = function (item) {
          if(typeof item === 'object') {
            return item.type === 'table';
          } else {
            return item.indexOf('[table]') > -1;
          }
        };

        $scope.isUnDisabledFields = function () {
          return activeFieldsList.length > 0;
        };

        $scope.openTableAttachment = function (id, taskId, isNew) {
          $scope.attachIsLoading = true;

          tasks.getTableOrFileAttachment(taskId, id, isNew).then(function (res) {
            $scope.openedAttachTable = typeof res === 'object' ? res : JSON.parse(res);
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

        /*
         * работа с таблицами
         */

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

        TableService.init($scope.taskForm);
        $scope.$on('TableFieldChanged', function(event, args) { $scope.updateTemplateList(); });

        //old service where we need to check the same id from form field and attachment to load it. remove it in a future.
        var idMatchInAttach = function () {
          angular.forEach($scope.taskForm, function (item, key, obj) {
            angular.forEach($scope.taskData.aAttachment, function (attachment) {
              var reg = /(\[id=(\w+)\])/;
              var match = attachment.description.match(reg);
              if(match !== null && (item.id && match[2].toLowerCase() === item.id.toLowerCase() ||item.name && match[2].toLowerCase() === item.name.toLowerCase())) {
                tasks.getTableOrFileAttachment(attachment.taskId, attachment.id).then(function (res) {
                  obj[key] = JSON.parse(res);
                  obj[key].description = attachment.description;
                })
              }
            })
          });
        };

        var newServiceExistedTableDownload = function () {
          angular.forEach($scope.taskForm, function (item, key, obj) {
            if(item.type === "table") {
              try {
                var isDBJSON = JSON.parse(item.value);
                if(isDBJSON && isDBJSON.sKey && isDBJSON.sID_StorageType) {
                  tasks.getTableOrFileAttachment($scope.taskData.oProcess.nID, item.id, true).then(function (res) {
                    if(res && res.id){
                      for(var t=0; t<$scope.taskData.aField.length; t++) {
                        var table = $scope.taskData.aField[t];
                        if(table.sID === res.id) {
                          res.writable = table.bWritable;
                          res.readable = table.bReadable;
                          res.required = table.bRequired;
                        }
                      }
                    }
                    obj[key] = res;
                  })
                }
              } catch (e){}
            }
          })
        };

        idMatchInAttach();
        newServiceExistedTableDownload();

        $scope.print = function (form, isMenuItem) {

          if( isMenuItem !== true ) { // Click on Button
            $scope.updateTemplateList();
          }

          if ( ( $scope.printTemplateList.length === 0 || isMenuItem === true ) && $scope.selectedTask && $scope.taskForm) {
            rollbackReadonlyEnumFields();
            $scope.printModalState.show = !$scope.printModalState.show;
          }
        };

        $scope.addRow = function (form, id, index) {
          ValidationService.validateByMarkers(form, $scope.markers, true, null, true);
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
        };

        $scope.tableIsLoaded = function (item) {
          return typeof item.aRow[0] !== 'number';
        };

        $scope.isVisible = function (field) {
          return TableService.isVisible(field);
        };

        $scope.searchingTablesForPrint = function () {
          angular.forEach($scope.taskData.aAttachment, function (attachment) {
            var tableID = attachment.description.match(/(\[id=(\w+)\])/);
            if(tableID !== null && tableID.length === 3) {
              tasks.getTableOrFileAttachment(attachment.taskId, attachment.id).then(function (res) {
                var table = JSON.parse(res);
                fixFieldsForTable(table);
                $scope.taskData.aTable.push(table);
              })
            }
          });
          $scope.updateTemplateList();
        };
        $scope.searchingTablesForPrint();

        /*
         * работа с таблицами
         */

        // проверка, есть ли поле в списке редактируемых (в документе).
        $scope.isDocumentWritable = function (field) {
          if(documentRights) {
            return documentRights.asID_Field_Write.indexOf(field.id)!== -1;
          } else {
            return true;
          }
        };

        // проверка, есть ли поле в списке для чтения (в документе).
        $scope.isDocumentReadable = function (field) {
          if(documentRights) {
            return documentRights.asID_Field_Read.indexOf(field.id)!== -1;
          } else {
            return true;
          }
        };

        // показывать поля только для чтения.
        $scope.showReadableField = function (field) {
          if($scope.isFormPropertyDisabled(field) && $scope.isDocumentReadable(field)) return true;
          else if(!$scope.isDocumentWritable(field) && $scope.isDocumentReadable(field)) return true;
          else if($scope.isFormPropertyDisabled(field) && $scope.isDocumentWritable(field)) return true;
        };

        // отображать поле в зависимости от доступности к чтению/записи документа.
        $scope.showField = function (field) {
          if(isJSONinHistory(field)) return false;
          if(documentRights) {
            if($scope.isDocumentReadable(field) || $scope.isDocumentWritable(field)) return true;
            else if(!$scope.isDocumentReadable(field) && !$scope.isDocumentWritable(field)) return false;
            else if(!$scope.inUnassigned() && $scope.isFormPropertyDisabled(field) && $scope.isDocumentWritable(field) && !$scope.isDocumentReadable(field)) return false;
            else if(!$scope.isFormPropertyDisabled(field) && ($scope.isDocumentWritable(field) || $scope.isDocumentReadable(field))) return true;
          } else {
            return true
          }
        };

        function isJSONinHistory (field) {
          return $scope.sSelectedTask === 'finished' && angular.isString(field.value) && field.value.length > 0 && (
            (field.value.charAt(0) === '{' && field.value.charAt(field.value.length - 1) === '}') ||
            (field.value.charAt(0) === '[' && field.value.charAt(field.value.length - 1) === ']'));
        }

        $scope.openUsersHierarchy = function () {
          $scope.attachIsLoading = true;
          tasks.getProcessSubjectTree($scope.selectedTask.processInstanceId).then(function (res) {
            $scope.documentFullHierarchy = res;
            $scope.attachIsLoading = false;
            eaTreeViewFactory.setItems($scope.documentFullHierarchy.aProcessSubjectTree, $scope.$id);
          });

          $scope.usersHierarchyOpened = !$scope.usersHierarchyOpened;
        };

        $scope.assignAndSubmitDocument = function () {
          $scope.taskForm.isInProcess = true;

          tasks.assignTask($scope.selectedTask.id, Auth.getCurrentUser().id)
            .then(function (result) {
              $scope.submitTask(form, true);
            })
            .catch(defaultErrorHandler);
        };

        $scope.isDocumentNotSigned = function () {
          if(!documentRights) return true;
          var notSigned = $scope.documentLogins.filter(function (login) {
            return !login.sDate && login.aUser.length > 0;
          });
          var currentUser = $scope.getCurrentUserLogin();
          for(var i=0; i<notSigned.length; i++) {
            if(notSigned[i].aUser[0].sLogin === currentUser) {
              return true;
            }
          }
        };

        // блокировка кнопок выбора файлов на время выполнения процесса загрузки ранее выбранного файла
        $rootScope.isFileProcessUploading = {
          bState: false
        };

        $rootScope.switchProcessUploadingState = function () {
          $rootScope.isFileProcessUploading.bState = !$rootScope.isFileProcessUploading.bState;
          console.log("Switch $rootScope.isFileProcessUploading to " + $rootScope.isFileProcessUploading.bState);
        };

        $scope.viewTrustedHTMLContent = function (html) {
          return $sce.trustAsHtml(html);
        };
        $scope.getOrgData = function (code, id) {
          var fieldPostfix = id.replace('sID_SubjectOrgan_OKPO_', '');
          var keys = {activities:'sID_SubjectActionKVED',ceo_name:'sCEOName',database_date:'sDateActual',full_name:'sFullName',location:'sLocation',short_name:'sShortName'};
          function findAndFillOKPOFields(res) {
            angular.forEach(res.data, function (data, key) {
              if (key in keys) {
                for (var i=0; i<$scope.taskForm.length; i++) {
                  var prop = $scope.taskForm[i].id;
                  if (prop.indexOf(keys[key]) === 0) {
                    var checkPostfix = prop.split(/_/),
                      elementPostfix = checkPostfix.length > 1 ? checkPostfix.pop() : null;
                    if (elementPostfix !== null && elementPostfix === fieldPostfix)
                      if(prop.indexOf('sID_SubjectActionKVED') > -1) {
                        var onlyKVEDNum = data.match(/\d{1,2}[\.]\d{1,2}/),
                          onlyKVEDText = data.split(onlyKVEDNum)[1].trim(),
                          pieces = prop.split('_');
                        onlyKVEDNum.length !== 0 ? $scope.taskForm[i].value = onlyKVEDNum[0] : $scope.taskForm[i].value = data;

                        pieces.splice(0, 1, 'sNote_ID');
                        var autocompleteKVED = pieces.join('_');
                        if(prop === autocompleteKVED)
                          $scope.taskForm[i].value = onlyKVEDText;
                      } else {
                        $scope.taskForm[i].value = data;
                      }
                  }
                }
              }
            })
          }
          function clearFieldsWhenError() {
            for (var i=0; i<$scope.taskForm.length; i++) {
              var prop = $scope.taskForm[i].id;
              if ($scope.data.formData.params.hasOwnProperty(prop) && prop.indexOf('_SubjectOrgan_') > -1) {
                var checkPostfix = prop.split(/_/),
                  elementPostfix = checkPostfix.length > 1 ? checkPostfix.pop() : null;
                if (elementPostfix !== null && elementPostfix === fieldPostfix && prop.indexOf('sID_SubjectOrgan_OKPO') === -1)
                  $scope.taskForm[i].value = '';
              }
            }
          }
          if(code) {
            $scope.orgIsLoading = {status:true,field:id};
            tasks.getOrganizationData(code).then(function (res) {
              $scope.orgIsLoading = {status:false,field:id};
              if (res.data === '' || res.data.error) {
                clearFieldsWhenError();
              } else {
                findAndFillOKPOFields(res);
              }
            });
          }
        };

        $scope.isOKPOField = function (i) {
          if(i){
            var splitID = i.split(/_/);
            if (splitID.length === 4 && splitID[1] === 'SubjectOrgan' && splitID[2] === 'OKPO') {
              return true
            }
          }
        };

        $scope.getBpAndFieldID = function (field) {
          if($scope.taskData && $scope.taskData.oProcess && $scope.taskData.oProcess.sBP){
            return $scope.taskData.oProcess.sBP.split(':')[0] + "_--_" + field.id;
          } else {
            return field.id;
          }
        };

        $scope.getFullCellId = function(field, column, row){
          if($scope.taskData && $scope.taskData.oProcess && $scope.taskData.oProcess.sBP){
            return $scope.taskData.oProcess.sBP.split(':')[0] + "_--_" + field.id + "_--_" + "COL_" + field.aRow[0].aField[column].id + "_--_" + "ROW_" + row;
          } else {
            return field.id + "_--_" + "COL_" + field.aRow[0].aField[column].id + "_--_" + "ROW_" + row;
          }
        };

        $scope.switchDelegateMenu = function () {
          $rootScope.delegateSelectMenu = !$rootScope.delegateSelectMenu;
        };


        $scope.getBpAndFieldID = function (field) {
          if($scope.taskData && $scope.taskData.oProcess && $scope.taskData.oProcess.sBP){
            return $scope.taskData.oProcess.sBP.split(':')[0] + "_--_" + field.id;
          } else {
            return field.id;
          }
        };

        $scope.getFullCellId = function(field, column, row){
          if($scope.taskData && $scope.taskData.oProcess && $scope.taskData.oProcess.sBP){
            return $scope.taskData.oProcess.sBP.split(':')[0] + "_--_" + field.id + "_--_" + "COL_" + field.aRow[0].aField[column].id + "_--_" + "ROW_" + row;
          } else {
            return field.id + "_--_" + "COL_" + field.aRow[0].aField[column].id + "_--_" + "ROW_" + row;
          }
        };

        $rootScope.$broadcast("update-search-counter");
      }
    ])
})();
