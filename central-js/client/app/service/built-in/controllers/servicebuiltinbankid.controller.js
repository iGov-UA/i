angular.module('app').controller('ServiceBuiltInBankIDController', ['$sce', '$state', '$stateParams', '$scope', '$timeout',
    '$location', '$window', '$rootScope', '$http', '$filter', 'FormDataFactory', 'ActivitiService', 'ValidationService',
    'ServiceService', 'oService', 'oServiceData', 'BankIDAccount', 'activitiForm', 'formData', 'allowOrder', 'countOrder',
    'selfOrdersCount', 'AdminService', 'PlacesService', 'uiUploader', 'FieldAttributesService', 'iGovMarkers', 'service',
    'FieldMotionService', 'ParameterFactory', '$modal', 'FileFactory', 'DatepickerFactory', 'autocompletesDataFactory',
    'ErrorsFactory', 'taxTemplateFileHandler', 'taxTemplateFileHandlerConfig', 'SignFactory', 'TableService', 'LabelService',
    'MasterPassService', 'modalService', 'BanksResponses',
    function ($sce, $state, $stateParams, $scope, $timeout, $location, $window, $rootScope, $http, $filter,
              FormDataFactory, ActivitiService, ValidationService, ServiceService, oService, oServiceData,
              BankIDAccount, activitiForm, formData, allowOrder, countOrder, selfOrdersCount, AdminService,
              PlacesService, uiUploader, FieldAttributesService, iGovMarkers, service, FieldMotionService,
              ParameterFactory, $modal, FileFactory, DatepickerFactory, autocompletesDataFactory,
              ErrorsFactory, taxTemplateFileHandler, taxTemplateFileHandlerConfig, SignFactory, TableService, LabelService,
              MasterPassService, modalService, BanksResponses) {

      'use strict';

      FieldMotionService.reset();
      iGovMarkers.reset();
      iGovMarkers.init();

      var currentState = $state.$current;
      var isStyled = false;

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

      $scope.checkoutData = {};
      $scope.isOpenedCheckout = false;
      $scope.checkoutSpinner = false;
      $scope.selectedCard = null;
      $scope.checkoutConfirm = {status: 'checkout'};
      $scope.phoneVerify = {showVerifyButton: true, dialog: false, otp: '', confirmed: false, otpIsConfirmed: true};

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

      function fixTableFiles(formProperties) {
        angular.forEach(formProperties, function (prop) {
          if(prop.type === 'table') {
            angular.forEach(prop.aRow, function (row) {
              angular.forEach(row.aField, function (field, key, obj) {
                if(field.type === 'file' && field.value && field.value.id) {
                  obj[key].value = field.value.id;
                }
              })
            })
          }
        })
      }

      $scope.fieldHasVisibleType = function (field) {
        if(field && field.type) {
          return field.type !== 'markers' && field.type !== 'invisible';
        }
      };

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
                var bConvertToPDF = false;
                for(var fieldInd = $scope.activitiForm.formProperties.length - 1; fieldInd >= 0; fieldInd--){
                  if($scope.activitiForm.formProperties[fieldInd].id === 'form_signed'){
                    var aFormSignValues = $scope.activitiForm.formProperties[fieldInd].name.replace(/\s+/, "").split(';');
                    if (aFormSignValues.length > 2) {
                      var aFormSignValuesElements = aFormSignValues[2].split(',');
                      angular.forEach(aFormSignValuesElements, function (sCondition) {
                        if(sCondition === 'bPrintFormFileAsPDF=true'
                          || sCondition === 'bPrintFormFileAsPDF=True'
                          || sCondition === 'bPrintFormFileAsPDF=TRUE'){
                          bConvertToPDF = true;
                        }
                      })
                    }
                    break;
                  }
                }
                var signPath = ActivitiService.getSignFormPath(oServiceData, result.formID, oService, $scope.data.formData.params, bConvertToPDF);
                $window.location.href = $location.protocol() + '://' + $location.host() + ':' + $location.port() + signPath;
                //$window.location.href = $location.absUrl()
                //  + '?formID=' + result.formID
                //  + '&signedFileID=' + 1122333;
              })
        } else {
          $window.alert('No sign is needed');
        }
      };

      $scope.processForm = function (form, aFormProperties, signNeeded) {

        if(signNeeded !== undefined) {
          $scope.sign.checked = signNeeded;
        }

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

        for( var pay in $scope.data.formData.params ) {
          if($scope.data.formData.params.hasOwnProperty(pay) && pay.indexOf('sID_Pay_MasterPass') === 0) {
            if(!$scope.data.formData.params[pay].value) {
              $scope.createPayment();
              return false;
            } else
                break;
          }
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
              var C_DOC_CNT = data.soPatternFilled.match(/<C_DOC_CNT>(.*?)<\/C_DOC_CNT>/);
              if (C_DOC_CNT !== null && C_DOC_CNT.length == 2) {
                if('C_DOC_CNT' in $scope.data.formData.params) {
                  $scope.data.formData.params.C_DOC_CNT.value = C_DOC_CNT[1];
                }
              }
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
                if(result.formID && result.formID.indexOf('sKey') > -1) {
                  try {
                    result.formID = JSON.parse(result.formID).sKey;
                  } catch(e) {}
                }
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

        function formSubmit() {
          if ($scope.sign.checked) {
            $scope.fixForm(form, aFormProperties);
            $scope.signForm();
          } else if (!$scope.data.formData.params[taxTemplateFileHandlerConfig.oFile_XML_SWinEd]) {
            $scope.submitForm(form, aFormProperties);
          }
        }

        var fileHTMLFields = aFormProperties.filter(function (field) {
          return field.type === 'fileHTML';
        });

        if(fileHTMLFields.length > 0) {
          ActivitiService.uploadFileHTML($scope.data.formData.params, $scope.activitiForm.formProperties).then(function () {
            formSubmit();
          });
        }

        if (fileHTMLFields.length === 0) {
          formSubmit();
        }
      };

      $scope.formMarkers = iGovMarkers.getMarkers();
      $scope.activitiForm.formProperties.filter(function(item){return item.type === 'markers'}).forEach(function (field) {
        if (field.type === 'markers' && $.trim(field.value)) {
          var sourceObj = null;
          try {
            sourceObj = JSON.parse(field.value);
          } catch (ex) {
            console.log('markers attribute ' + field.name + ' contain bad formatted json\n' + ex.name + ', ' + ex.message + '\nfield.value: ' + field.value);
          }
          if (sourceObj !== null) {
            _.merge($scope.formMarkers, sourceObj, function (destVal, sourceVal) {
              if (_.isArray(sourceVal)) {
                return sourceVal;
              }
            });
          }
        }
      });

      $scope.validateForm = function (form) {
        var bValid = true;
        ValidationService.validateByMarkers(form, $scope.formMarkers, true, this.data.formData.params ? this.data.formData.params : {});
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
      $scope.slotsCache = {
        iGov: {},
        loadedList: {},
        showConfirm: false
      };

      $scope.submitForm = function (form, aFormProperties) {
        if (form) {
          form.$setSubmitted();
        }

        fixTableFiles(aFormProperties);
        $scope.fixForm(form, aFormProperties);
        var aReservedSlotsDMS = [];
        var aQueueOfIGov = [];
        if (aFormProperties && aFormProperties !== null) {
          angular.forEach(aFormProperties, function (oProperty) {
            if (oProperty.type === "enum" && oProperty.bVariable && oProperty.bVariable !== null && oProperty.bVariable === true) {//oProperty.id === attr.sName &&
              $scope.data.formData.params[oProperty.id].value = null;
            }
            if (oProperty.type === 'queueData' && $scope.data.formData.params[oProperty.id].value) {
              var needSend = true;
              angular.forEach(aFormProperties, function (checkField) {
                if (checkField.id === ('sID_Type_' + oProperty.id) && checkField.value === 'DMS') {
                  aReservedSlotsDMS.push(oProperty.id);
                  needSend = false;
                }
              });
              if(needSend){
                aQueueOfIGov.push(oProperty.id);
              }
            }
          });
        }

        for(var keySlotField = 0; keySlotField < aQueueOfIGov.length; keySlotField++){
          var bValid = true;
          var slot = angular.fromJson(this.data.formData.params[aQueueOfIGov[keySlotField]].value);
          var cachedSlot = this.slotsCache.iGov[slot.nID_FlowSlotTicket];
          if(bValid && cachedSlot.nID_SubjectOrganDepartment){
            bValid = bValid && this.data.formData.params[cachedSlot.sDepartmentField].value === cachedSlot.nID_SubjectOrganDepartment;
          }
          if(bValid && cachedSlot.sID_Public_SubjectOrganJoin){
            bValid = bValid && this.data.formData.params.sID_Public_SubjectOrganJoin.nID === cachedSlot.sID_Public_SubjectOrganJoin;
          }

          if(!bValid){
            $scope.slotsCache.showConfirm = true;
            this.data.formData.params[aQueueOfIGov[keySlotField]].value = null;
            if(form[aQueueOfIGov[keySlotField]]){
              form[aQueueOfIGov[keySlotField]].$modelValue = null;
              form[aQueueOfIGov[keySlotField]].$viewValue = null;
            }
            $rootScope.$broadcast("reset-slot-picker");
            ErrorsFactory.push({
              type: 'danger',
              text: 'Під час реєстрації вашого талону в електронній черзі були змінені реєстраційні дані. Будь ласка, повторіть свій вибір.'
            });
            $scope.isSending = false;
            return;
          }
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

      	if( isStyled == false ) {

      	  FieldAttributesService.enableStyles();

      	  isStyled = true;
      	}

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
        if(property.type === 'queueData' && !bVisible && $scope.data.formData.params[property.id].value !== null){
          $scope.data.formData.params[property.id].value = null;
        }

        if(property.options && property.options.hasOwnProperty('bVisible')){
          bVisible = bVisible && property.options['bVisible'];
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
          $scope.isSignNeededRequired = b;
          if(!($scope.isSignNeeded && !$scope.isSignNeededRequired)){
            $scope.sign.checked = b;
          }
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

      $scope.isShowFillSelfPrevious = false;
      $scope.fillSelfPrevious = function () {

        $http.get('/api/order/getStartFormByTask', {
          params: {
            nID_Service: oService.nID,
            sID_UA: oServiceData.oPlaceRoot ? oServiceData.oPlaceRoot.sID_UA : oServiceData.oPlace ? oServiceData.oPlace.sID_UA : ''
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
            	$scope.isShowFillSelfPrevious = true;
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

      // відображення напису про необхідність перевірки реєстраційних данних, переданих від BankID
      $scope.isShowMessageRequiringToValidateUserData = function () {
        if ($scope.isFormDataEmpty()) {
          return false;
        } else {
          return BankIDAccount.customer.sUsedAuthType === 'bankid' || BankIDAccount.customer.sUsedAuthType === 'bankid-nbu';
        }
      };

      // https://github.com/e-government-ua/i/issues/1325
      $scope.getBpAndFieldID = function (field) {
        return this.oServiceData.oData.processDefinitionId.split(':')[0] + "_--_" + field.id;
      };

      $scope.getFullCellId = function(field, column, row){
        return this.oServiceData.oData.processDefinitionId.split(':')[0] + "_--_" + field.id + "_--_" + "COL_" + field.aRow[0].aField[column].id + "_--_" + "ROW_" + row;
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
      function openUrl(url, post, target) {
        if (post) {
          var form = $('<form/>', {
            action: url,
            method: 'POST',
            target: target ? target : '_blank',
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
        ValidationService.validateByMarkers(form, $scope.formMarkers, true, this.data.formData.params ? this.data.formData.params : {}, true);
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

      if ($scope.selfOrdersCount.nOpened > 0 && oServiceData.oPlace || oServiceData.oPlaceRoot) {
        $scope.fillSelfPrevious();
      }

      $rootScope.queue = {
        previousOrganJoin: {}
      };

      /*
        *поиски организации по окпо начало
      */
      $scope.getOrgData = function (code, id) {
        var fields = Object.keys($scope.data.formData.params);
        var fieldPostfix = id.replace('sID_SubjectOrgan_OKPO_', '');
        var keys = {activities:'sID_SubjectActionKVED',ceo_name:'sCEOName',database_date:'sDateActual',full_name:'sFullName',location:'sLocation',short_name:'sShortName'};
        function findAndFillOKPOFields(res) {
          angular.forEach(res.data, function (i, key) {
            if (key in keys) {
              for (var prop in $scope.data.formData.params) {
                if ($scope.data.formData.params.hasOwnProperty(prop) && prop.indexOf(keys[key]) === 0) {
                  var checkPostfix = prop.split(/_/),
                    elementPostfix = checkPostfix.length > 1 ? checkPostfix.pop() : null;
                  if (elementPostfix !== null && elementPostfix === fieldPostfix)
                    if(prop.indexOf('sID_SubjectActionKVED') > -1) {
                      var onlyKVEDNum = i.match(/\d{1,2}[\.]\d{1,2}/),
                          onlyKVEDText = i.split(onlyKVEDNum)[1].trim(),
                          pieces = prop.split('_');
                      onlyKVEDNum.length !== 0 ? $scope.data.formData.params[prop].value = onlyKVEDNum[0] : $scope.data.formData.params[prop].value = i

                      pieces.splice(0, 1, 'sNote_ID');
                      var autocompleteKVED = pieces.join('_');
                      $scope.data.formData.params[autocompleteKVED].value = onlyKVEDText;
                    } else {
                      $scope.data.formData.params[prop].value = i;
                    }
                }
              }
            }
          })
        }
        function clearFieldsWhenError() {
          for (var prop in $scope.data.formData.params) {
            if ($scope.data.formData.params.hasOwnProperty(prop) && prop.indexOf('_SubjectOrgan_') > -1) {
              var checkPostfix = prop.split(/_/),
                elementPostfix = checkPostfix.length > 1 ? checkPostfix.pop() : null;
              if (elementPostfix !== null && elementPostfix === fieldPostfix && prop.indexOf('sID_SubjectOrgan_OKPO') === -1)
                $scope.data.formData.params[prop].value = '';
            }
          }
        }
        if(code) {
          $scope.orgIsLoading = {status:true,field:id};
          ServiceService.getOrganizationData(code).then(function (res) {
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
      /*
       *поиски организации по окпо конец
      */

      $scope.labelStyle = function (field) {
        return LabelService.labelStyle(field);
      };

      $scope.isSetClasses = function (field) {
        return LabelService.isLabelHasClasses(field)
      };

      /*verify phone number start*/
      $scope.phoneVerifyStart = function () {
        var phoneNumber = MasterPassService.searchValidPhoneNumber($scope.data.formData.params);
        MasterPassService.phoneCheck(phoneNumber, null).then(function () {
          $scope.phoneVerify.dialog = true;
        });
      };

      $scope.confirmOtp = function () {
        $scope.checkoutSpinner = true;
        var phoneNumber = MasterPassService.searchValidPhoneNumber($scope.data.formData.params);
        MasterPassService.otpPhoneConfirm(phoneNumber, $scope.phoneVerify.otp).then(function (res) {
          $scope.phoneVerify.confirmed = $scope.phoneVerify.otpIsConfirmed = res;
          if (res)
            $scope.authorizeCheckout();
          else {
            $scope.checkoutSpinner = false;

          }
        });
      };

      $scope.changePhone = function () {
        $scope.data.formData.params['phone'].value = '+380';
        $scope.phoneVerify = {showVerifyButton: true, dialog: false, otp: '', confirmed: false, otpIsConfirmed: true};
        $scope.isOpenedCheckout = false;
      };
      /*verify phone number end*/

      /*MasterPass Checkout start*/
      function searchMPCheckoutFields() {
        $scope.checkoutData = MasterPassService.fillCheckoutData($scope.activitiForm.formProperties);
      }

      $scope.isMPassField = function (id, all) {
        if(id && !all)
          return MasterPassService.isMasterPassButton(id, all);
        if(!id && all)
          return MasterPassService.isMasterPassButton(false, $scope.activitiForm.formProperties);
      };

      $scope.authorizeCheckout = function () {
        $scope.checkoutSpinner = true;
        $scope.paymentStatus = null;
        var phoneNumber = MasterPassService.searchValidPhoneNumber($scope.data.formData.params);

        if (phoneNumber && phoneNumber.length === 12 && $scope.phoneVerify.confirmed) {
          MasterPassService.checkUser(phoneNumber, 'ua').then(function (res) {
            if(res) {
              $scope.isOpenedCheckout = true;
              $scope.phoneVerify.dialog = $scope.phoneVerify.showVerifyButton = false;
              if(res.url) {
                $scope.userCards = null;
                $scope.registerLink = res.url;
              } else if(res.error) {
                $scope.paymentStatus = 4;
                console.error(res.error);
              } else {
                $scope.userCards = res;
                $scope.selectedCard = res[Object.keys(res)[0]];
                $scope.registerLink = null;
              }
            }
            $scope.checkoutConfirm = {status: 'checkout'};
            $scope.checkoutSpinner = false;
          });
        } else if (phoneNumber && phoneNumber.length === 12 && !$scope.phoneVerify.confirmed){
          $scope.isOpenedCheckout = false;
          var modalOptions = MasterPassService.messages('phone-is-not-verified');
          modalService.showModal(modalOptions.defaults, modalOptions.modal)
        } else {
          $scope.isOpenedCheckout = false;
        }
      };
      $scope.changeCard = function (i) {
        $scope.selectedCard = i;
      };

      $scope.createPayment = function () {
        $scope.checkoutSpinner = true;
        var phoneNumber = MasterPassService.searchValidPhoneNumber($scope.data.formData.params);

        searchMPCheckoutFields();
        $scope.checkoutData.card_alias = $scope.selectedCard.card_alias;

        if(phoneNumber && phoneNumber.length === 12) {
          MasterPassService.createPayment(phoneNumber, $scope.checkoutData).then(function (res) {
            if(res.pmt_status == 4) {
              $scope.paymentStatus = 4;
            } else if(res.pmt_status == 0) {
              if (res.secure && res.secure === '3ds') {
                var url = $location.protocol() + '://' + $location.host() + ':' + $location.port() + $location.path();
                var callbackUrl = $location.protocol() + '://' + $location.host() + ':' + $location.port() + '/api/masterpass/verify3DSCallback' + '?id=' + res.pmt_id + '&url=' + url;
                var temp = JSON.stringify({form: $scope.data.formData.params, activiti: $scope.activitiForm.formProperties});
                localStorage.setItem('temporaryForm', temp);

                openUrl(res.ascUrl, {pareq: res.pareq, md: res.md, TermUrl: callbackUrl}, '_self');
              } else if(res.secure && res.secure === 'otp') {
                $scope.phoneVerify.otpIsConfirmed = true;
                $scope.otpErrorMsg = null;
                $scope.checkoutConfirm.status = 'confirm';
                $scope.checkoutData.payment= {otpToken: res.token, otpCode: '', invoice: res.invoice, pmt_id: res.pmt_id};
              }
            } else if(res.pmt_status == 5) {
              MasterPassService.paymentSale($scope.checkoutData.payment).then(function (res) {
                if(res.pmt_status == 4) {
                  $scope.paymentStatus = 4;
                } else if(res.pmt_status == 5) {
                  $scope.paymentStatus = 5;
                  for(var field in $scope.data.formData.params) {
                    if($scope.data.formData.params.hasOwnProperty(field) && field.indexOf('sID_Pay_MasterPass') === 0) {
                      $scope.data.formData.params[field].value = res.pmt_id;
                      $scope.checkoutData.payment = {result: res.pmt_id};
                    }
                  }
                }
              })
            }
            $scope.checkoutSpinner = false;
          })
        } else {
          $scope.checkoutSpinner = false;
        }
      };

      function otpError() {
        $scope.isSending = false;
        $scope.paymentStatus = 4;
        $scope.checkoutConfirm.status = 'checkout';
        $scope.checkoutSpinner = false;
      }

      $scope.otpConfirmPayment = function () {
        $scope.checkoutSpinner = true;
        var phoneNumber = MasterPassService.searchValidPhoneNumber($scope.data.formData.params);
        MasterPassService.otpConfirm($scope.checkoutData.payment.otpCode, $scope.checkoutData.payment.otpToken, phoneNumber).then(function (res) {
          if(res.status === 'OK') {
            MasterPassService.paymentSale($scope.checkoutData.payment).then(function (res) {
              if(res.pmt_status == 4) {
                otpError();
              } else if(res.pmt_status == 5) {
                $scope.paymentStatus = 5;
                for(var field in $scope.data.formData.params) {
                  if($scope.data.formData.params.hasOwnProperty(field) && field.indexOf('sID_Pay_MasterPass') === 0) {
                    $scope.data.formData.params[field].value = res.pmt_id;
                    $scope.checkoutData.payment = {result: res.pmt_id};
                    $scope.processForm($scope.myForm, activitiForm.formProperties, false);
                  }
                }
              }
            })
          } else if(res.error === 'otp max attempts') {
            otpError();
          } else if(res.error) {
            $scope.phoneVerify.otpIsConfirmed = $scope.checkoutSpinner =  false;
            $scope.otpErrorMsg = MasterPassService.otpErrorMessages(res.error);
          } else {
            otpError();
          }
        });
      };

      $scope.chooseAnotherCard = function () {
        $scope.authorizeCheckout();
        $scope.checkoutConfirm = {status: 'checkout'};
        $scope.checkoutData.payment = {};
      };

      var checkLocation = $location.url();
      var tempFiles = localStorage.getItem('temporaryForm');

      function getTemporarySavedFields() {
        var parsedFormData = JSON.parse(tempFiles).form;
        var parsedActivitiForm = JSON.parse(tempFiles).activiti;

        for( var param in parsedFormData ) {
          if(parsedFormData.hasOwnProperty(param) && $scope.data.formData.params.hasOwnProperty(param)) {
            $scope.data.formData.params[param].value = parsedFormData[param].value;
          } else if(parsedFormData.hasOwnProperty(param) && $scope.data.formData.params.hasOwnProperty(param) && 'aRow' in parsedFormData[param]) {
            $scope.data.formData.params[param].aRow = parsedFormData[param].aRow;
          }
        }

        for( var i=0; i<parsedActivitiForm.length; i++ ) {
          for( var j=0; j<$scope.activitiForm.formProperties.length; j++) {
            if(parsedActivitiForm[i].id === $scope.activitiForm.formProperties[j].id) {
              $scope.activitiForm.formProperties[j].value = parsedActivitiForm[i].value;
            } else if(parsedActivitiForm[i].id === $scope.activitiForm.formProperties[j].id && parsedActivitiForm[i].aRow) {
              $scope.activitiForm.formProperties[j].aRow = parsedActivitiForm[i].aRow;
            }
          }
        }

        $scope.phoneVerify.dialog = $scope.phoneVerify.showVerifyButton = false;
        $scope.phoneVerify.confirmed = true;
        localStorage.removeItem('temporaryForm');
      }

      if(checkLocation.indexOf('pmt_id') > -1) {
        var parse = checkLocation.split('?')[1], data = parse.split('&'), paymentStatus = data[0].split('=')[1], paymentID = data[1].split('=')[1];
        $scope.paymentStatus = paymentStatus;
        $scope.checkoutData.payment = {result: paymentID};

        if (tempFiles)
          getTemporarySavedFields();

        for (var field in $scope.data.formData.params) {
          if($scope.data.formData.params.hasOwnProperty(field) && field.indexOf('sID_Pay_MasterPass') === 0) {
            $scope.data.formData.params[field].value = paymentID;
            $timeout(function () {
              $scope.processForm($scope.myForm, activitiForm.formProperties, false);
            });
          }
        }

      } else if(checkLocation.indexOf('3DS') > -1 && checkLocation.indexOf('failed') > -1) {
        if (tempFiles) {
          getTemporarySavedFields();
          $scope.authorizeCheckout();
        }
      } else if(checkLocation.indexOf('status=failed') > -1 && checkLocation.indexOf('bank_response') > -1) {
        var r = checkLocation.split('?')[1], query = r.split('&'), bId = query[1].split('=')[1], bError = query[2].split('=')[1];
        BanksResponses.getErrorMessage(bId, bError).then(function (res) {
          if (tempFiles) {
            getTemporarySavedFields();
            $scope.paymentStatus = 4;
            $scope.checkoutErrorMsg = res.data;
          }
        });
      }

      $scope.setFormToScope = function (form) {
        $scope.myForm = form;
      }
      /*MasterPass Checkout end*/
}]);
