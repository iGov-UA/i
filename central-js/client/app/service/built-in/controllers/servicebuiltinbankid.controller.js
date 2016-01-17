angular.module('app').controller('ServiceBuiltInBankIDController', function(
  $sce,
  $state,
  $stateParams,
  $scope,
  $timeout,
  $location,
  $window,
  $rootScope,
  $http,
  FormDataFactory,
  ActivitiService,
  ValidationService,
  oService,
  oServiceData,
  BankIDAccount,
  activitiForm,
  allowOrder,
  countOrder,
  selfOrdersCount,
  AdminService,
  PlacesService,
  uiUploader,
  FieldAttributesService,
  MarkersFactory,
  service,
  FieldMotionService,
  $modal) {

  'use strict';

  var currentState = $state.$current;

  $scope.paramsBackup = null
  
  $scope.oServiceData = oServiceData;
  $scope.account = BankIDAccount; // FIXME потенційний хардкод
  $scope.activitiForm = activitiForm;
  $scope.countOrder = countOrder;
  $scope.selfOrdersCount = selfOrdersCount;

  $scope.data = $scope.data || {};

  $scope.data.region = currentState.data.region;
  $scope.data.city = currentState.data.city;
  $scope.data.id = currentState.data.id;

  $scope.setFormScope = function(scope){
    this.formScope = scope;
  };

  var initializeFormData = function (){
    $scope.data.formData = new FormDataFactory();
    $scope.data.formData.initialize($scope.activitiForm);
    $scope.data.formData.setBankIDAccount(BankIDAccount);
    $scope.data.formData.uploadScansFromBankID(oServiceData);
  };

  if (!allowOrder) {
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
          return "Уже достигнуто число одновременно поданных и не закрытых заявок " +
            "Вами по данной услуге для данного места. Вы можете перейти в закладку \"Статусы\" (например, https://igov.org.ua/order/search) " +
            "где найдя одну из своих заявок - написать комментарий сотруднику по ней, для ее скорейшей отработке " +
            "или закрытия.(чтоб разблокировать дельнейшую подачу)";
        }
      }
    });
  }

  if ( !$scope.data.formData ) {
    initializeFormData();
  }

  $scope.markers = ValidationService.getValidationMarkers();
  var aID_FieldPhoneUA = $scope.markers.validate.PhoneUA.aField_ID;

  $scope.referent = false;
  angular.forEach($scope.activitiForm.formProperties, function(field) {

    var sFieldName = field.name || '';

    // 'Як працює послуга; посилання на інструкцію' буде розбито на частини по ';'
    var aNameParts = sFieldName.split(';');
    var sFieldNotes = aNameParts[0].trim();

    field.sFieldLabel = sFieldNotes;

    sFieldNotes = null;

    if (aNameParts.length > 1) {
      sFieldNotes = aNameParts[1].trim();
      if (sFieldNotes === '') {
        sFieldNotes = null;
      }
    }
    field.sFieldNotes = sFieldNotes;

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
        _.merge(MarkersFactory.getMarkers(), sourceObj, function(destVal, sourceVal) {
          if (_.isArray(sourceVal)) {
            return sourceVal;
          }
        });
      }
    }
  });
  MarkersFactory.validateMarkers();
  //save values for each property
  $scope.persistValues = JSON.parse(JSON.stringify($scope.data.formData.params));
  $scope.getSignFieldID = function(){
    return data.formData.getSignField().id;
  };

  $scope.isSignNeeded = $scope.data.formData.isSignNeeded();
  $scope.isSignNeededRequired = $scope.data.formData.isSignNeededRequired();
  //$scope.sign = {checked : false };
  $scope.sign = {checked : $scope.data.formData.isSignNeededRequired() };

  $scope.signForm = function () {
    if($scope.data.formData.isSignNeeded){
      ActivitiService.saveForm(oService, oServiceData,
        'some business key 111',
        'process name here', $scope.activitiForm, $scope.data.formData)
        .then(function (result) {
          var signPath = ActivitiService.getSignFormPath(oServiceData, result.formID, oService);
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
    $scope.isSending = true;

    if (!$scope.validateForm(form)) {
      $scope.isSending = false;
      return false;
    }

    if ($scope.sign.checked) {
      $scope.signForm();
    } else {
      $scope.submitForm(form, aFormProperties);
    }
  };

  $scope.validateForm = function(form) {
    var bValid = true;
    ValidationService.validateByMarkers(form, null, true);
    return form.$valid && bValid;
  };

  $scope.submitForm = function(form, aFormProperties) {
    if(form){
      form.$setSubmitted();
    }


        console.log("aFormProperties="+(aFormProperties&&aFormProperties!==null));
        if(aFormProperties && aFormProperties!==null){
            angular.forEach(aFormProperties, function(oProperty){
                console.log("oProperty.id="+oProperty.id+",oProperty.type="+oProperty.type+",oProperty.bVariable="+oProperty.bVariable);
                //oProperty.enumValues = a;
                //if(oProperty.type === "enum" && oProperty.enumValues && oProperty.enumValues != null && oProperty.enumValues.length == 0){//oProperty.id === attr.sName &&
                if(oProperty.type === "enum" && oProperty.bVariable && oProperty.bVariable !== null && oProperty.bVariable === true){//oProperty.id === attr.sName &&
                    console.log('oProperty.type === "enum" && oProperty.enumValues && oProperty.enumValues != null && oProperty.enumValues.length == 0');
                    $scope.data.formData.params[oProperty.id].value=null;
                }
            });
        }


    ActivitiService
      .submitForm(oService, oServiceData, $scope.data.formData)
      .then(function(result) {
        $scope.isSending = false;

        var state = $state.$current;

        var submitted = $state.get(state.name + '.submitted');
        if (!result.id) {
          // console.log(result);
          return;
        }
        //TODO: Fix Alhoritm Luna
        var nCRC = ValidationService.getLunaValue(result.id);

        submitted.data.id = oServiceData.nID_Server + "-" + result.id + nCRC; //11111111
        //angular.forEach(BankIDAccount.customer, function (oValue, sKey) {
        //return angular.forEach(BankIDAccount.customer, function (oValue, sKey) {
        /*
        var oaField = $scope.data.formData.params;
        console.log("aField="+(oaField && oaField!==null));
        if(oaField && oaField!==null){
            angular.forEach(oaField, function(oValue, sKey){
                console.log("sKey="+sKey+",oValue="+(oValue && oValue!==null));
                if(oValue && oValue!==null){
                    console.log("sKey="+sKey+",oValue.id="+oValue.id+",oValue.value="+oValue.value+",oValue.sCustomType="+oValue.sCustomType);
                    if(oValue.sCustomType === "enum"){//oProperty.id === attr.sName &&
                        console.log('oValue.sCustomType === "enum"');
                        oValue.value=null;
                        //$scope.data.formData.params[oField.id].value=null;
                    }
                }
            });
        }
          */
        /*
        console.log("aEnum="+(aEnum&&aEnum!==null));
        var aEnum = $scope.data.formData.aEnum;
        if(aEnum && aEnum!==null){
            angular.forEach(aEnum, function(oEnum){
                $scope.data.formData.params[oEnum.id].value=null;
            });
        }
        */

        /*if(aFormProperties && aFormProperties!==null){
        for (var id in $scope.data.formData.params) {
          var value = $scope.data.formData.params[id];

        }*/

        //angular.forEach($scope.data.formData.params, function(oParams){

        //});


        submitted.data.formData = $scope.data.formData;

        $scope.isSending = false;

        $scope.$root.data = $scope.data;

        return $state.go(submitted, angular.extend($stateParams, {formID: null, signedFileID : null}));
      });
  };

  $scope.cantSubmit = function(form) {
    return $scope.isSending
      || ($scope.isUploading && !form.$valid)
      || ($scope.isSignNeeded && !$scope.sign.checked);
  };

  $scope.bSending = function(form) {
    return $scope.isSending;
  };

  $scope.isUploading = false;
  $scope.isSending = false;

  function getFieldProps(property) {
    if ($scope.referent && property.id.startsWith('bankId')){
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

  $scope.onReferent = function (){
    if ($scope.referent){

      angular.forEach($scope.activitiForm.formProperties, function (field){
        //if (field.id.startsWith('bankId') && field.type !== 'file'){
        if (field.id.startsWith('bankId')){
          $scope.data.formData.params[field.id].value="";
            /*if (field.type === 'file'){
                $scope.data.formData.params[field.id].upload = true;
                $scope.data.formData.params[field.id].scan = null;
            }*/
        }
        if (field.type === 'file'){
            $scope.data.formData.params[field.id].value="";
            $scope.data.formData.params[field.id].upload = true;
            $scope.data.formData.params[field.id].scan = null;
        }
      });

      /*if ($scope.data.formData.params['bankId_scan_passport']){
        $scope.data.formData.params['bankId_scan_passport'].upload = true;
        $scope.data.formData.params['bankId_scan_passport'].scan = null;
      }*/

      $scope.data.formData.initialize($scope.activitiForm);

    } else {
      initializeFormData();
    }
  };

  $scope.showFormField = function(property) {
    var p = getFieldProps(property);
    if ($scope.referent && property.id.startsWith('bankId')){
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

  $scope.renderAsLabel = function(property) {
    if ($scope.referent && property.id.startsWith('bankId')){
      return false;
    }
    var p = getFieldProps(property);
    if (p.mentionedInWritable)
      return !FieldMotionService.isFieldWritable(property.id, $scope.data.formData.params);
    //property.type !== 'file'
    return (
      $scope.data.formData.fields[property.id] && p.fieldES === p.ES.NOT_SET
      ) || p.fieldES === p.ES.READ_ONLY ;
  };

  $scope.isFieldVisible = function(property) {
    return property.id !== 'processName' && (FieldMotionService.FieldMentioned.inShow(property.id) ?
      FieldMotionService.isFieldVisible(property.id, $scope.data.formData.params) : true);
  };

  $scope.isFieldRequired = function(property) {
    if ($scope.referent && property.id == 'bankId_scan_passport'){
      return true;
    }
    var b=FieldMotionService.FieldMentioned.inRequired(property.id) ?
      FieldMotionService.isFieldRequired(property.id, $scope.data.formData.params) : property.required;
    return b;
  };

  $scope.$watch('data.formData.params', watchToSetDefaultValues, true);
  function watchToSetDefaultValues() {
    var calcFields = FieldMotionService.getCalcFieldsIds();
    var pars = $scope.data.formData.params;
    calcFields.forEach(function(key) {
      if (_.has(pars, key)) {
        var data = FieldMotionService.calcFieldValue(key, pars);
        if (data.value && data.differentTriggered) pars[key].value = data.value;
      }
    });
  }

  $scope.htmldecode = function(encodedhtml)
  {
    var map = {
      '&amp;'     :   '&',
      '&gt;'      :   '>',
      '&lt;'      :   '<',
      '&quot;'    :   '"',
      '&#39;'     :   "'"
    };

    var result = angular.copy(encodedhtml);
    angular.forEach(map, function(value, key)
    {
      while(result.indexOf(key) > -1)
        result = result.replace(key, value);
    });

    return result;
  };

  $scope.getHtml = function(html) {
    return $sce.trustAsHtml(html);
  };

  if($scope.data.formData.isAlreadySigned() && $stateParams.signedFileID){
    var state = $state.$current;
    //TODO remove ugly hack for not calling submit after submit
    if(!state.name.endsWith('submitted')){
      $scope.submitForm();
    }
  }

  $scope.isFormDataEmpty = function() {
    for (var param in $scope.data.formData.params ) {
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
        sID_UA: oServiceData.oPlace.sID_UA
      }
    }).then(function (response) {
      var bFilled = bFilledSelfPrevious();
      if(!bFilled){
        $scope.paramsBackup = {};
      }
      angular.forEach($scope.data.formData.params, function (property, key) {
        if (response.data.hasOwnProperty(key) && key && key !== null && key.indexOf("bankId") !== 0){
            if(!bFilled){
                $scope.paramsBackup[key] = property.value;
            }
            property.value = response.data[key];
        }
      });
    });
  };

  $scope.bFilledSelfPrevious = function () {
      return $scope.paramsBackup !== null;
      
  };
  
  $scope.fillSelfPreviousBack = function () {
      var bFilled = bFilledSelfPrevious();
      if(bFilled){
        angular.forEach($scope.data.formData.params, function (property, key) {
            property.value = $scope.paramsBackup[key];
        });
        $scope.paramsBackup = null;
      }
  };
  
  if($scope.selfOrdersCount.nOpened > 0){
    $scope.fillSelfPrevious();
  }

});
