angular.module('app').controller('ServiceBuiltInBankIDController',
    function ($sce, $state, $stateParams, $scope, $timeout, $location, $window, $rootScope, $http, $filter,
              FormDataFactory, ActivitiService, ValidationService, ServiceService, oService, oServiceData,
              BankIDAccount, activitiForm, formData, allowOrder, countOrder, selfOrdersCount, AdminService,
              PlacesService, uiUploader, FieldAttributesService, iGovMarkers, service, FieldMotionService,
              ParameterFactory, $modal, FileFactory, DatepickerFactory, autocompletesDataFactory,
              ErrorsFactory, taxTemplateFileHandler, taxTemplateFileHandlerConfig, SignFactory, TableService) {

      'use strict';

      var currentState = $state.$current;

      $scope.paramsBackup = null;

      $scope.oServiceData = oServiceData;
      $scope.account = BankIDAccount; // FIXME потенційний хардкод
      $scope.activitiForm = activitiForm;
      $scope.countOrder = countOrder;
      $scope.selfOrdersCount = selfOrdersCount;

      $scope.data = $scope.data || {};

      $scope.data.checkbox = {};

      $scope.data.region = currentState.data.region;
      $scope.data.city = currentState.data.city;
      $scope.data.id = currentState.data.id;

      $scope.data.formData = formData;
      $scope.tableIsInvalid = false;

      $scope.setFormScope = function (scope) {
        this.formScope = scope;
      };

      var initializeFormData = function () {
        $scope.data.formData = new FormDataFactory();
        return $scope.data.formData.initialize($scope.activitiForm, BankIDAccount, oServiceData);
      };


      if (!$scope.data.id && !allowOrder) {
        $location.path("/");
        var modalInstance = $modal.open({
          animation: true,
          size: 'md',
          templateUrl: 'app/service/allowOrderModal.html',
          controller: function ($scope, $modalInstance, message) {
            $scope.message = message;

            $scope.close = function () {
              $modalInstance.close();
            }
          },
          resolve: {
            message: function () {
              return "Вже досягнуто число одночасно поданих та не закритих заяв " +
                  "Вами по даній послузі для цієї місцевості. Ви можете перейти на вкладку \"Мій журнал\" (наприклад, https://igov.org.ua/order/search) " +
                  "де знайшовши одну зі своїх заяв - написати по ній комментар співробітнику, для її найшвидшого опрацювання " +
                  "або закриття (щоб розблокувати подальше подання).";
            }
          }
        });
      }

      $scope.bAdmin = AdminService.isAdmin();
      $scope.markers = ValidationService.getValidationMarkers();
      var aID_FieldPhoneUA = $scope.markers.validate.PhoneUA.aField_ID;
      var formFieldIDs = {
        inForm: [],
        inMarkers: []
      };

      angular.forEach($scope.activitiForm.formProperties, function (field) {

        var sFieldName = field.name || '';

        // 'Як працює послуга; посилання на інструкцію' буде розбито на частини по ';'
        var aNameParts = sFieldName.split(';');
        var sFieldNotes = aNameParts[0].trim();
        var checkbox = getCheckbox(aNameParts[2]);

        if (checkbox) {
          bindEnumToCheckbox({
            id: field.id,
            enumValues: field.enumValues,
            sID_CheckboxTrue: checkbox.sID_CheckboxTrue
          });
        }

        field.sFieldLabel = sFieldNotes;

        sFieldNotes = null;

        if (aNameParts.length > 1) {
          sFieldNotes = aNameParts[1].trim();
          if (sFieldNotes === '') {
            sFieldNotes = null;
          }
        }
        field.sFieldNotes = sFieldNotes;

        if (checkbox && field.type === 'enum') {
          field.type = 'checkbox';
        }
        // перетворити input на поле вводу телефону, контрольоване директивою form/directives/tel.js:
        if (_.indexOf(aID_FieldPhoneUA, field.id) !== -1) {
          field.type = 'tel';
          field.sFieldType = 'tel';
        }
        if (field.type === 'markers' && $.trim(field.value)) {
          var sourceObj = null;
          try {
            sourceObj = JSON.parse(field.value);
          } catch (ex) {
            console.log('markers attribute ' + field.name + ' contain bad formatted json\n' + ex.name + ', ' + ex.message + '\nfield.value: ' + field.value);
          }
          if (sourceObj !== null) {
            if (sourceObj.validate) {
              for (var key1 in sourceObj.validate) if (sourceObj.validate.hasOwnProperty(key1)) {
                var tempV = formFieldIDs.inMarkers;
                if (sourceObj.validate[key1].aField_ID) {
                  formFieldIDs.inMarkers = tempV.concat(sourceObj.validate[key1].aField_ID);
                } else if (sourceObj.validate[key1].sID_Field) {
                  formFieldIDs.inMarkers = tempV.concat(sourceObj.validate[key1].sID_Field);
                }
              }
            } else if (sourceObj.motion) {
              for (var key2 in sourceObj.motion) if (sourceObj.motion.hasOwnProperty(key2)) {
                var tempM = formFieldIDs.inMarkers;
                if (sourceObj.motion[key2].aField_ID) {
                  formFieldIDs.inMarkers = tempM.concat(sourceObj.motion[key2].aField_ID);
                } else if (sourceObj.motion[key2].sID_Field) {
                  formFieldIDs.inMarkers = tempM.concat(sourceObj.motion[key2].sID_Field);
                }
              }
            } else if (sourceObj.attributes) {
              for (var key3 in sourceObj.attributes) if (sourceObj.attributes.hasOwnProperty(key3)) {
                var tempA = formFieldIDs.inMarkers;
                if (sourceObj.attributes[key3].aField_ID) {
                  formFieldIDs.inMarkers = tempA.concat(sourceObj.attributes[key3].aField_ID);
                } else if (sourceObj.attributes[key3].sID_Field) {
                  formFieldIDs.inMarkers = tempA.concat(sourceObj.attributes[key3].sID_Field);
                }
              }
            }
            _.merge(iGovMarkers.getMarkers(), sourceObj, function (destVal, sourceVal) {
              if (_.isArray(sourceVal)) {
                return sourceVal;
              }
            });
          }
        }
        if (field.id === 'bReferent') {
          angular.extend($scope.data.formData.params.bReferent, field);
          $scope.visibleBReferent = true;
          switch ($scope.data.formData.params.bReferent.value) {
            case 'true':
              $scope.data.formData.params.bReferent.value = true;
              break;
            case 'false':
              $scope.data.formData.params.bReferent.value = false;
              break;
          }
        }
        formFieldIDs.inForm.push(field.id);
      });

      function getCheckbox(param) {
        if (!param || !typeof param === 'string') return null;

        var input = param.trim(),
            finalArray,
            result = {};

        var checkboxExp = input.split(',').filter(function (item) {
          return (item && typeof item === 'string' ? item.trim() : '')
                  .split('=')[0]
                  .trim() === 'sID_CheckboxTrue';
        })[0];

        if (!checkboxExp) return null;

        finalArray = checkboxExp.split('=');

        if (!finalArray || !finalArray[1]) return null;

        var indexes = finalArray[1].trim().match(/\d+/ig),
            index;

        if (Array.isArray(indexes)) {
          index = isNaN(+indexes[0]) || +indexes[0];
        }

        result[finalArray[0].trim()] = index !== undefined
        && index !== null
        || index === 0 ? index : finalArray[1].trim();

        return result;
      }

      function bindEnumToCheckbox(param) {
        if (!param || !param.id || !param.enumValues ||
            param.sID_CheckboxTrue === null ||
            param.sID_CheckboxTrue === undefined) return;

        var trueValues,
            falseValues;

        if (isNaN(+param.sID_CheckboxTrue)) {
          trueValues = param.enumValues.filter(function (o) {
            return o.id === param.sID_CheckboxTrue
          });
          falseValues = param.enumValues.filter(function (o) {
            return o.id !== param.sID_CheckboxTrue
          });
          $scope.data.checkbox[param.id] = {
            trueValue: trueValues[0] ? trueValues[0].id : null,
            falseValue: falseValues[0] ? falseValues[0].id : null
          };
        } else {
          falseValues = param.enumValues.filter(function (o, i) {
            return i !== param.sID_CheckboxTrue
          });
          $scope.data.checkbox[param.id] = {
            trueValue: param.enumValues[param.sID_CheckboxTrue] ?
                param.enumValues[param.sID_CheckboxTrue].id : null,
            falseValue: falseValues[0] ? falseValues[0].id : null
          };
        }
      }

      iGovMarkers.validateMarkers(formFieldIDs);
      //save values for each property
      $scope.persistValues = JSON.parse(JSON.stringify($scope.data.formData.params));
      $scope.getSignFieldID = function () {
        return data.formData.getSignField().id;
      };

      $scope.isSignNeeded = $scope.data.formData.isSignNeeded();
      $scope.isSignNeededRequired = $scope.data.formData.isSignNeededRequired();
      //$scope.sign = {checked : false };
      $scope.sign = {checked: $scope.data.formData.isSignNeededRequired()};

      $scope.signForm = function () {
        if ($scope.data.formData.isSignNeeded) {
          ActivitiService.saveForm(oService, oServiceData,
              'some business key 111',
              'process name here', $scope.activitiForm, $scope.data.formData)
              .then(function (result) {
                var signPath = ActivitiService.getSignFormPath(oServiceData, result.formID, oService, $scope.data.formData.params);
                $window.location.href = $location.protocol() + '://' + $location.host() + ':' + $location.port() + signPath;
                //$window.location.href = $location.absUrl()
                //  + '?formID=' + result.formID
                //  + '&signedFileID=' + 1122333;
              })
        } else {
          $window.alert('No sign is needed');
        }
      };

      $scope.processForm = function (form, aFormProperties) {

        angular.forEach($scope.activitiForm.formProperties, function (prop) {
          if (prop.type === 'table') {
            $scope.data.formData.params[prop.id].value = prop;
          }
        });

        angular.forEach(aFormProperties, function (i) {
          if (i.type === 'select' &&
              i.hasOwnProperty('autocompleteData') &&
              $scope.data.formData.params[i.id].value &&
              $scope.data.formData.params[i.id].value.hasOwnProperty(i.autocompleteData.valueProperty)) {
            $scope.data.formData.params[i.id].value = $scope.data.formData.params[i.id].value[i.autocompleteData.valueProperty]
          }
        });

        $scope.isSending = true;

        if (!$scope.validateForm(form)) {
          $scope.isSending = false;
          return false;
        }

        /**
         * If oFile_XML_SWinEd property exists - try to handle taxTemplateFileHandler
         * https://github.com/e-government-ua/i/issues/1374
         */
        if($scope.data.formData.params[taxTemplateFileHandlerConfig.oFile_XML_SWinEd]){
          taxTemplateFileHandler.postJSON({
            formProperties: aFormProperties,
            formData: $scope.data.formData,
            oServiceData: oServiceData
          }, function (data, error) {

            if (data) {
              $scope.data.formData.params[taxTemplateFileHandlerConfig.soPatternFilled] = data.soPatternFilled;

              angular.extend($scope.data.formData, {
                contentToSign: {
                  contentValue: $scope.data.formData.params[taxTemplateFileHandlerConfig.soPatternFilled],
                  contentName: data.sFileName
                }
              });

              ActivitiService.saveForm(
                  oService,
                  oServiceData,
                  'some business key 111',
                  'process name here',
                  $scope.activitiForm,
                  $scope.data.formData
              ).then(function (result) {
                $window.location.href =
                    $location.protocol() + '://' +
                    $location.host() + ':' +
                    $location.port() + '/api/sign-content/sign?formID=' +
                    result.formID + '&nID_Server=' +
                    oServiceData.nID_Server + '&sName=' + oService.sName;
              });
            }

            if(error){
              ErrorsFactory.push({
                type: 'danger',
                text: error.text + JSON.stringify(error.value)
              })
            }
          });
        }


        if ($scope.sign.checked) {
          $scope.fixForm(form, aFormProperties);
          $scope.signForm();
        } else if (!$scope.data.formData.params[taxTemplateFileHandlerConfig.oFile_XML_SWinEd]) {
          $scope.submitForm(form, aFormProperties);
        }
      };

      $scope.validateForm = function (form) {
        var bValid = true;
        ValidationService.validateByMarkers(form, null, true, this.data);
        return form.$valid && bValid;
      };

      $scope.fixForm = function (form, aFormProperties) {
        try {
          if (aFormProperties && aFormProperties !== null) {
            angular.forEach(aFormProperties, function (oProperty) {
              if ((oProperty.id === "sVarLastName_0001" || oProperty.id === "sVarLastName_0002" || oProperty.id === "sVarLastName_0003")
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params["bankIdlastName"].value;
              }
              if ((oProperty.id === "sVarFirstName_0001" || oProperty.id === "sVarFirstName_0002" || oProperty.id === "sVarFirstName_0003")
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params["bankIdfirstName"].value;
              }
              if ((oProperty.id === "sVarMiddleName_0001" || oProperty.id === "sVarMiddleName_0002" || oProperty.id === "sVarMiddleName_0003")
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params["bankIdmiddleName"].value;
              }
              if ((oProperty.id === "sVarDatDenN_0001" || oProperty.id === "sVarDatDenN_0002" || oProperty.id === "sVarDatDenN_0003")
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params["bankIdbirthDay"].value;
              }
              if ((oProperty.id === "sReestrNum_0001" || oProperty.id === "sReestrNum_0002" || oProperty.id === "sReestrNum_0003")
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params["bankIdinn"].value;
              }

              var s = "";

              s = "sVarPostIndex";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarOblNam";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarRajOblNam";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarPlaceNam";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarRajCityNam";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarVulNam";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarBudNum";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarKvNum";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarTypePom";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

              s = "sVarPomNum";
              if ((oProperty.id === (s + "_0001") || oProperty.id === (s + "_0002") || oProperty.id === (s + "_0003"))
                  && ($scope.data.formData.params[oProperty.id].value === null || $scope.data.formData.params[oProperty.id].value === "")) {//oProperty.id === attr.sName &&
                $scope.data.formData.params[oProperty.id].value = $scope.data.formData.params[s].value;
              }

            });
          }
        } catch (sError) {
          console.log('[submitForm.fixForm]sError=' + sError);
        }

      };

      $scope.submitForm = function (form, aFormProperties) {
        if (form) {
          form.$setSubmitted();
        }

        $scope.fixForm(form, aFormProperties);
        var aReservedSlotsDMS = [];
        if (aFormProperties && aFormProperties !== null) {
          angular.forEach(aFormProperties, function (oProperty) {
            if (oProperty.type === "enum" && oProperty.bVariable && oProperty.bVariable !== null && oProperty.bVariable === true) {//oProperty.id === attr.sName &&
              $scope.data.formData.params[oProperty.id].value = null;
            }
            if (oProperty.type === 'queueData' && $scope.data.formData.params[oProperty.id].value) {
              angular.forEach(aFormProperties, function (checkField) {
                if (checkField.id === ('sID_Type_' + oProperty.id) && checkField.value === 'DMS') {
                  aReservedSlotsDMS.push(oProperty.id);
                }
              });
            }
          });
        }

        if (aReservedSlotsDMS.length > 0) {
          setSlotsDMS(aReservedSlotsDMS, 0, aFormProperties);
        } else {
          submitActivitiForm(aFormProperties);
        }
      };

      function setSlotsDMS(aQueueIDs, iteration, aFormProperties) {
        var reserve = JSON.parse($scope.data.formData.params[aQueueIDs[iteration]].value);

        $http.post('/api/service/flow/DMS/setSlot', {
          nID_Server: oServiceData.nID_Server,
          nID_SlotHold: parseInt(reserve.reserve_id)
        }).success(function (data, status, headers, config) {
          console.log(data);

          $scope.data.formData.params[aQueueIDs[iteration]].value = JSON.stringify({
            sID_Type: "DMS",
            sDate: data.date_time + '.00',
            nID_ServiceCustomPrivate: parseInt(data.service_id),
            ticket_number: data.ticket_number,
            ticket_code: data.ticket_code
          });

          if (iteration < aQueueIDs.length - 1) {
            setSlotsDMS(aQueueIDs, iteration + 1, aFormProperties);
          } else {
            submitActivitiForm(aFormProperties);
          }
        }).error(function (data, status, headers, config) {
          console.error(data);
          ErrorsFactory.push({
            type: 'danger',
            text: 'Неможливо зарезервувати час в електронній черзі ДМС.'
          })
        });
      }

      function submitActivitiForm(aFormProperties) {

        $scope.spinner = true;

        ActivitiService
            .submitForm(oService, oServiceData, $scope.data.formData, aFormProperties)//$scope.activitiForm
            .then(function (oReturn) {
              $scope.isSending = false;
              var state = $state.$current;
              var submitted = $state.get(state.name + '.submitted');

              var oFuncNote = {sHead: "Сабміт форми послуги", sFunc: "submitForm(UI)"};
              ErrorsFactory.init(oFuncNote, {asParam: ['nID_Service: ' + oService.nID, 'nID_ServiceData: ' + oServiceData.nID, 'processDefinitionId: ' + oServiceData.oData.processDefinitionId]});

              if (!oReturn) {
                ErrorsFactory.logFail({sBody: "Повернен пустий об'ект!"});
                return;
              }
              if (!oReturn.id) {
                ErrorsFactory.logFail({
                  sBody: "У поверненому об'єкті немає номера створеної заявки!",
                  asParam: ["soReturn: " + JSON.stringify(oReturn)]
                });
                return;
              }

              var nCRC = ValidationService.getLunaValue(oReturn.id);
              var sID_Order = oServiceData.nID_Server + "-" + oReturn.id + nCRC;
              submitted.data.id = sID_Order;

              submitted.data.formData = $scope.data.formData;
              $scope.isSending = false;
              $scope.$root.data = $scope.data;
              if ($scope.data.formData.params.email) {
                $rootScope.data.email = $scope.data.formData.params.email.value;
              }

              try {
//            ErrorsFactory.logInfoSendHide({sType:"success", sBody:"Створена заявка!",asParam:["sID_Order: "+sID_Order]});
              } catch (sError) {
                console.log('[submitForm.ActivitiService]sID_Order=' + sID_Order + ',sError=' + sError);
              }

              return $state.go(submitted, angular.extend($stateParams, {formID: null, signedFileID: null}))
                .then(function(){
                  $scope.spinner = false;
                });
            });
      }

      $scope.cantSubmit = function (form) {
        return $scope.isSending
            || ($scope.isUploading && !form.$valid)
            || ($scope.isSignNeeded && !$scope.sign.checked);
      };

      $scope.bSending = function (form) {
        return $scope.isSending;
      };

      $scope.isUploading = false;
      $scope.isSending = false;

      function getFieldProps(property) {
        if ($scope.data.formData.params.bReferent.value && property.id.startsWith('bankId')) {
          return {
            mentionedInWritable: true,
            fieldES: 1, //EDITABLE
            ES: FieldAttributesService.EditableStatus
          }
        }
        return {
          mentionedInWritable: FieldMotionService.FieldMentioned.inWritable(property.id),
          fieldES: FieldAttributesService.editableStatusFor(property.id),
          ES: FieldAttributesService.EditableStatus
        };
      }

      $scope.onReferent = function (oldV, newV) {
        if ($scope.data.formData.params.bReferent.value) {

          angular.forEach($scope.activitiForm.formProperties, function (field) {
            //if (field.id.startsWith('bankId') && field.type !== 'file'){
            if (field.id.startsWith('bankId')) {
              $scope.data.formData.params[field.id].value = "";
              /*if (field.type === 'file'){
               $scope.data.formData.params[field.id].upload = true;
               $scope.data.formData.params[field.id].scan = null;
               }*/
            }
            if (field.type === 'file') {
              $scope.data.formData.params[field.id].value = "";
              //$scope.data.formData.params[field.id].upload = true;
              $scope.data.formData.params[field.id].scan = null;
            }
          });

          /*if ($scope.data.formData.params['bankId_scan_passport']){
           $scope.data.formData.params['bankId_scan_passport'].upload = true;
           $scope.data.formData.params['bankId_scan_passport'].scan = null;
           }*/

          $scope.data.formData.initializeParamsOnly($scope.activitiForm);

        } else {
          initializeFormData();
        }
      };

      $scope.showFormField = function (property) {
        var p = getFieldProps(property);
        if ($scope.data.formData.params.bReferent.value && property.id.startsWith('bankId')) {
          return true;
        }
        if (p.mentionedInWritable)
          return FieldMotionService.isFieldWritable(property.id, $scope.data.formData.params);

        return (
            !$scope.data.formData.fields[property.id]
            && property.type !== 'invisible'
            && property.type !== 'markers'
            && p.fieldES === p.ES.NOT_SET ) || p.fieldES === p.ES.EDITABLE;
      };

      $scope.renderAsLabel = function (property) {
        if ($scope.data.formData.params.bReferent.value && property.id.startsWith('bankId')) {
          return false;
        }
        var p = getFieldProps(property);
        if (p.mentionedInWritable)
          return !FieldMotionService.isFieldWritable(property.id, $scope.data.formData.params);
        //property.type !== 'file'
        return (
                $scope.data.formData.fields[property.id] && p.fieldES === p.ES.NOT_SET
            ) || p.fieldES === p.ES.READ_ONLY;
      };

      $scope.isFieldVisible = function (property) {
        var bVisible = property.id !== 'processName' && (FieldMotionService.FieldMentioned.inShow(property.id) ?
            FieldMotionService.isFieldVisible(property.id, $scope.data.formData.params) : true);
        if($scope.data.formData.params[property.id] instanceof SignFactory){
          $scope.isSignNeeded = bVisible;
        }
        return bVisible;
      };

      $scope.isFieldRequired = function (property) {
        if ($scope.data.formData.params.bReferent.value && property.id == 'bankId_scan_passport') {
          return true;
        }
        var b = FieldMotionService.FieldMentioned.inRequired(property.id) ?
            FieldMotionService.isFieldRequired(property.id, $scope.data.formData.params) : property.required;
        if($scope.data.formData.params[property.id] instanceof SignFactory){
          $scope.sign.checked = b;
          $scope.isSignNeededRequired = b;
        }
        return b;
      };

      $scope.$watch('data.formData.params', watchToSetDefaultValues, true);
      function watchToSetDefaultValues() {
        //var calcFields = FieldMotionService.getCalcFieldsIds();
        var calcFields = FieldMotionService.getTargetFieldsIds('Values');
        var pars = $scope.data.formData.params;
        calcFields.forEach(function (key) {
          if (_.has(pars, key)) {
            var data = FieldMotionService.calcFieldValue(key, pars, $scope.activitiForm.formProperties);
            if (data.value && data.differentTriggered) pars[key].value = data.value;
          }
        });

        var requiredCalcFields = FieldMotionService.getTargetFieldsIds('Required');
        requiredCalcFields.forEach(function (key) {
          if (_.has(pars, key)) {
            var dataRequired = FieldMotionService.isFieldRequired(key, pars);
            if (pars[key].required != dataRequired) {
              pars[key].required = dataRequired;
              if (pars[key] instanceof SignFactory){
                $scope.sign.checked = dataRequired;
                $scope.isSignNeededRequired = dataRequired;
              }
            }
          }
        });
      }

      $scope.htmldecode = function (encodedhtml) {
        if (encodedhtml) {
          var map = {
            '&amp;': '&',
            '&gt;': '>',
            '&lt;': '<',
            '&quot;': '"',
            '&#39;': "'"
          };

          var result = angular.copy(encodedhtml);
          angular.forEach(map, function (value, key) {
            while (result.indexOf(key) > -1)
              result = result.replace(key, value);
          });

          return result;
        } else {
          return encodedhtml;
        }
      };

      $scope.getHtml = function (html) {
        return $sce.trustAsHtml(html);
      };

      /**
       * Check for $scope.data.formData.params[taxTemplateFileHandlerConfig.oFile_XML_SWinEd] was done
       * in order to https://github.com/e-government-ua/i/issues/1374
       */
      if (($scope.data.formData.isAlreadySigned() &&
          $stateParams.signedFileID) ||
          ($stateParams.signedFileID &&
          $scope.data.formData.params[taxTemplateFileHandlerConfig.oFile_XML_SWinEd])) {
        var state = $state.$current;


        //TODO should be refactored signedFileID assignment after tax template file partial signing (https://github.com/e-government-ua/i/issues/1374)
        $scope.data.formData.params[taxTemplateFileHandlerConfig.oFile_XML_SWinEd] ?
            $scope.data.formData.params[taxTemplateFileHandlerConfig.oFile_XML_SWinEd].value = $stateParams.signedFileID :
            null;

        //TODO remove ugly hack for not calling submit after submit
        if (!state.name.endsWith('submitted')) {
          $scope.submitForm();
        }
      }

      $scope.isFormDataEmpty = function () {
        for (var param in $scope.data.formData.params) {
          if ($scope.data.formData.params.hasOwnProperty(param) &&
              $scope.data.formData.params[param].hasOwnProperty('value') &&
              $scope.data.formData.params[param]['value'] != null) {
            return false;
          }
        }
        return true;
      };

      $scope.fillSelfPrevious = function () {

        $http.get('/api/order/getStartFormByTask', {
          params: {
            nID_Service: oService.nID,
            sID_UA: oServiceData.oPlaceRoot ? oServiceData.oPlaceRoot.sID_UA : oServiceData.oPlace.sID_UA
          }
        }).then(function (response) {
          var bFilled = $scope.bFilledSelfPrevious();
          if (!bFilled) {
            $scope.paramsBackup = {};
          }
          angular.forEach($scope.activitiForm.formProperties, function (oField) {
            try {
              var key = oField.id;
              var property = $scope.data.formData.params[key];

              if (key && key !== null && key.indexOf("bankId") !== 0 && response.data.hasOwnProperty(key)) {

                if (oField && oField !== null
                    && oField.type !== "file"
                    && oField.type !== "label"
                    && oField.type !== "invisible"
                    && oField.type !== "markers"
                    && oField.type !== "queueData"
                    && oField.type !== "select"
                ) {
                  if (!bFilled) {
                    $scope.paramsBackup[key] = property.value;
                  }
                  property.value = response.data[key];
                }

                if (oField.type === 'select' &&
                    oField.hasOwnProperty('autocompleteData')) {
                  property.value = {};
                  property.value[oField.autocompleteData.valueProperty] = response.data[key];
                }
              }
            } catch (_) {
              console.log("[fillSelfPrevious][" + key + "]ERROR:" + _);
            }
          });
        });
      };

      $scope.bFilledSelfPrevious = function () {
        return $scope.paramsBackup !== null;

      };

      $scope.fillSelfPreviousBack = function () {
        if ($scope.bFilledSelfPrevious()) {
          angular.forEach($scope.data.formData.params, function (property, key) {
            // && $scope.paramsBackup[key] && $scope.paramsBackup[key]!==null && $scope.paramsBackup[key] !== undefined
            if (key && key !== null && key.indexOf("bankId") !== 0 && $scope.paramsBackup.hasOwnProperty(key)) {
              //console.log("RESTORE:property.value="+property.value);
              property.value = $scope.paramsBackup[key];
              //console.log("RESTORE:paramsBackup["+key+"]="+$scope.paramsBackup[key]);
            }
          });
          $scope.paramsBackup = null;
        }
      };

      $scope.insertSeparator = function(sPropertyId){
        return FieldAttributesService.insertSeparators(sPropertyId);
      };

      // блокировка кнопок выбора файлов на время выполнения процесса загрузки ранее выбранного файла
      $rootScope.isFileProcessUploading = {
        bState: false
      };

      $rootScope.switchProcessUploadingState = function () {
        $rootScope.isFileProcessUploading.bState = !$rootScope.isFileProcessUploading.bState;
        console.log("Switch $rootScope.isFileProcessUploading to " + $rootScope.isFileProcessUploading.bState);
      };

      if ($scope.selfOrdersCount.nOpened > 0 && oServiceData.oPlace || oServiceData.oPlaceRoot) {
        $scope.fillSelfPrevious();
      }

      // відображення напису про необхідність перевірки реєстраційних данних, переданих від BankID
      $scope.isShowMessageRequiringToValidateUserData = function () {
        if ($scope.isFormDataEmpty()) {
          return false;
        } else {
          return BankIDAccount.customer.isAuthTypeFromBankID;
        }
      };

      // https://github.com/e-government-ua/i/issues/1325
      $scope.getBpAndFieldID = function (field) {
        return this.oServiceData.oData.processDefinitionId.split(':')[0] + "_--_" + field.id;
      };

      // https://github.com/e-government-ua/i/issues/1326
      $scope.redirectPaymentLiqpay = function (sMerchantFieldID) {
        var incorrectLiqpayRequest = false;
        var sSuffix = sMerchantFieldID.substring('sID_Merchant'.length);
        var paramsLiqPay = {
          sID_Merchant: $scope.data.formData.params[sMerchantFieldID].value
        };
        var merchantId = "sSum" + sSuffix;
        if ($scope.data.formData.params[merchantId] && $scope.data.formData.params[merchantId].value > 0) {
          paramsLiqPay.sSum = $scope.data.formData.params[merchantId].value;
        } else {
          console.warn("redirectPaymentLiqpay sSum value not found");
          incorrectLiqpayRequest = true;
        }
        merchantId = "sID_Currency" + sSuffix;
        if ($scope.data.formData.params[merchantId] && $scope.data.formData.params[merchantId].value !== null && $scope.data.formData.params[merchantId].value !== "") {
          paramsLiqPay.sID_Currency = $scope.data.formData.params[merchantId].value;
        }
        merchantId = "sDescription" + sSuffix;
        if ($scope.data.formData.params[merchantId] && $scope.data.formData.params[merchantId].value !== null && $scope.data.formData.params[merchantId].value !== "") {
          paramsLiqPay.sDescription = $scope.data.formData.params[merchantId].value;
        }
        paramsLiqPay.nID_Server = this.oServiceData.nID_Server;
        var sCurrDateTime = $filter('date')(new Date(), 'yyyy-MM-dd_HH:mm:ss.sss');
        paramsLiqPay.sID_Order = this.oService.nID + "--" + this.oServiceData.oData.processDefinitionId.split(':')[0] + "--" + sCurrDateTime;
        if (!incorrectLiqpayRequest) {
          $http.get('api/payment-liqpay', {
            params: angular.copy(paramsLiqPay)
          }).success(function (data) {
            openUrl(data.sURL, {
              data: data.data,
              signature: data.signature
            });
          })
        }
      };

      // отправка POST-запроса и открытие страницы в новой вкладке
      function openUrl(url, post) {
        if (post) {
          var form = $('<form/>', {
            action: url,
            method: 'POST',
            target: '_blank',
            style: {
              display: 'none'
            }
          });

          for (var key in post) if (post.hasOwnProperty(key)) {
            form.append($('<input/>', {
              type: 'hidden',
              name: key,
              value: post[key]
            }));
          }

          form.appendTo(document.body); // Необходимо для некоторых браузеров
          form.submit();
        } else {
          window.open(url, '_blank');
        }
      }

      TableService.init($scope.activitiForm.formProperties);

      $scope.addRow = function (form, id, index) {
        ValidationService.validateByMarkers(form, null, true, this.data.formData.params ? this.data.formData.params : {}, true);
        if (!form.$invalid) {
          $scope.tableIsInvalid = false;
          TableService.addRow(id, $scope.activitiForm.formProperties);
        } else {
          $scope.tableIsInvalid = true;
          $scope.invalidTableNum = index;
        }
      };
      $scope.removeRow = function (index, form, id) {
        TableService.removeRow($scope.activitiForm.formProperties, index, id);
        if (!form.$invalid) {
          $scope.tableIsInvalid = false;
        }
      };
      $scope.rowLengthCheckLimit = function (table) {
        return TableService.rowLengthCheckLimit(table);
      };

      $scope.isFieldWritable = function (field) {
        return TableService.isFieldWritable(field);
      };
    });
