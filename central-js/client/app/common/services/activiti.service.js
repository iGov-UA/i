angular.module('app').service('ActivitiService', function ($q, $http, $location, ErrorsFactory) {

  var prepareFormData = function (oService, oServiceData, formData, nID_Server) {//url
    var data = {
      'nID_Server': nID_Server
    };

    var nID_Region;
    var sID_UA;
    var sID_UA_Common;

    if(oServiceData.nID_Region){
      nID_Region = oServiceData.nID_Region.nID;
      sID_UA = oServiceData.nID_Region.sID_UA;
      sID_UA_Common = oServiceData.nID_Region.sID_UA;
    } else if (oServiceData.nID_City){
      nID_Region = oServiceData.nID_City.nID_Region.nID;
      sID_UA = oServiceData.nID_City.nID_Region.sID_UA;
      sID_UA_Common = oServiceData.nID_City.sID_UA;
    }

    var params = {
      nID_Service : oService.nID,
      nID_Region : nID_Region,
      sID_UA : sID_UA,
      sID_UA_Common : sID_UA_Common
    };

    data = angular.extend(data, formData.getRequestObject());
    data = angular.extend(data, params);

    return data;
  };

  this.getForm = function (oServiceData, processDefinitionId) {
    var oFuncNote = {sHead:"Отримяня форми послуги", sFunc:"getForm"};
    var oData = {
      'nID_Server': oServiceData.nID_Server
      , 'sID_BP_Versioned': processDefinitionId.sProcessDefinitionKeyWithVersion
    };
    ErrorsFactory.init(oFuncNote, {asParam:['nID_ServiceData: '+oServiceData.nID, 'sID_BP_Versioned: '+oData.sID_BP_Versioned]});
    return $http.get('./api/process-form', {
      params: oData,
      data: oData
    }).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse)){
            return oResponse.data;
        }
    });
  };

  this.submitForm = function (oService, oServiceData, formData) {
    var oFuncNote = {sHead:"Сабміт форми послуги", sFunc:"submitForm"};
    var nID_Server = oServiceData.nID_Server;
    var oFormData = prepareFormData(oService, oServiceData, formData, nID_Server);
    ErrorsFactory.init(oFuncNote, {asParam: ['nID_Service: '+oService.nID, 'nID_ServiceData: '+oServiceData.nID, 'processDefinitionId: '+oServiceData.oData.processDefinitionId, "soFormData: "+JSON.stringify(oFormData)]});
    return $http.post('./api/process-form', oFormData).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse,function(doMerge, sMessage, aCode, sResponse){
            if (!sMessage) {
            } else if (sMessage.indexOf('happened when sending email') > -1) {
                doMerge({sBody: 'Помилка при відсилці єлектронної пошти! (скоріш за все не вірні дані вказані у формі чи електроний адрес)'});
            } else if (sMessage.indexOf('Exception while invoking TaskListener') > -1) {
                doMerge({sBody: 'Помилка при обробці листенера! (скоріш за все не вірні дані вказані у формі)'});
            } else if (sMessage.indexOf("For input string") > -1) {
                doMerge({sBody: 'Помилка при обробці строкового поля форми! (скоріш за все не вірні дані вказані у формі)'});
            } else if (sMessage.indexOf("Invalid value for") > -1) {
                doMerge({sBody: 'Помилка при обробці значення поля форми! (скоріш за все не вірні дані вказані у формі)'});
            }                    
        })){
            return oResponse.data;
        }
      /*if (/err/i.test(response.data.code)) {
          //ErrorsFactory.addFail({""})
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }*/
    });
  };

  this.loadForm = function (oServiceData, formID) {
    var oFuncNote = {sHead:"Завантаженя форми послуги", sFunc:"loadForm"};
    ErrorsFactory.init(oFuncNote,{asParam: ['nID_ServiceData: '+oServiceData.nID, 'formID: '+formID]});
    var oParams = {nID_Server: oServiceData.nID_Server, formID: formID};
    return $http.get('./api/process-form/load', {params: oParams}).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse)){
            return oResponse.data;
        }
      /*if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }*/
    });
  };

  this.saveForm = function (oService, oServiceData, businessKey, processName, activitiForm, formData) {
    var oFuncNote = {sHead:"Збереження форми послуги", sFunc:"saveForm"};
    var nID_Server = oServiceData.nID_Server;
    var oFormData = prepareFormData(oService, oServiceData, formData, nID_Server);
    ErrorsFactory.init(oFuncNote, {asParam: ['nID_Service: '+oService.nID, 'nID_ServiceData: '+oServiceData.nID, 'processName: '+processName, 'businessKey: '+businessKey, 'soFormData: '+JSON.stringify(oFormData)]});
    var oData = {
      formData : oFormData,
      activitiForm: activitiForm,
      processName : processName,
      businessKey : businessKey
    };
    var restoreFormUrl = $location.absUrl();
    var oParams = {
      nID_Server : nID_Server
    };
    oData = angular.extend(oData, {
      restoreFormUrl: restoreFormUrl
    });
    return $http.post('./api/process-form/save', oData, {params : oParams}).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse)){
            return oResponse.data;
        }
      /*if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }*/
    });
  };

  this.getSignFormPath = function (oServiceData, formID, oService) {
    return '/api/process-form/sign?formID=' + formID + '&nID_Server=' + oServiceData.nID_Server + '&sName=' + oService.sName;

  };

  this.getUploadFileURL = function (oServiceData) {
    return './api/uploadfile?nID_Server=' + oServiceData.nID_Server;
  };

  this.updateFileField = function (oServiceData, formData, propertyID, fileUUID) {
    formData.params[propertyID].value = fileUUID;
  };

  this.checkFileSign = function (oServiceData, fileID){
    var oFuncNote = {sHead:"Перевірка ЕЦП у файлу", sFunc:"checkFileSign"};
    ErrorsFactory.init(oFuncNote, {asParam: ['nID_ServiceData: '+oServiceData.nID, 'fileID: '+fileID]});
    return $http.get('./api/process-form/sign/check', {
      params : {
        fileID : fileID,
        nID_Server : oServiceData.nID_Server
      }
    }).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse)){
            return oResponse.data;
        }
    }).catch(function (error) {
        if(!ErrorsFactory.bSuccessResponse(error)){
            return $q.reject(error.data);
        }
      /*ErrorsFactory.push({
        type: "danger",
        text: [error.data.code, error.data.message].join(" ")
      });*/
    });
  };

  this.autoUploadScans = function (oServiceData, scans) {
    var oFuncNote = {sHead:"Завантаженя файлу", sFunc:"autoUploadScans"};
    ErrorsFactory.init(oFuncNote, {asParam: ['nID_ServiceData: '+oServiceData.nID, 'scans: '+scans]});
    var oData = {
      nID_Server: oServiceData.nID_Server,
      scanFields: scans
    };
    return $http.post('./api/process-form/scansUpload', oData).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse)){
            return oResponse.data;
        }
        /*if (/err/i.test(response.data.code)) {
          ErrorsFactory.push({
            type: "danger",
            text: [response.data.code, response.data.message].join(" ")
          });
        }*/
    });
  };
});
