angular.module('app').service('ActivitiService', function ($q, $http, $location, ErrorsFactory) {

  var prepareFormData = function (oService, oServiceData, formData, nID_Server) {//url
    var data = {
      //'url': url
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
    ErrorsFactory.init(oFuncNote);
    //var url = oServiceData.sURL + oServiceData.oData.sPath + '?processDefinitionId=' + processDefinitionId.sProcessDefinitionKeyWithVersion;
    var data = {
      //-//'url': url
      'nID_Server': oServiceData.nID_Server
      , 'sID_BP_Versioned': processDefinitionId.sProcessDefinitionKeyWithVersion
    };
    return $http.get('./api/process-form', {
      params: data,
      data: data
    }).then(function (response) {
        var asParam=['nID_ServiceData: '+oServiceData.nID, 'sID_BP_Versioned: '+data.sID_BP_Versioned];
        if(ErrorsFactory.bSuccessResponse(response.data,function(sResponseMessage){
            if (sResponseMessage) {
                return {sType: "error", sBody: 'Помилка при запросі!', asParam:asParam};
            } else {
                return {sType: "error", sBody: 'Невідома помилка при запросі!', asParam:asParam};
            }                    
        })){
            
        }
        ErrorsFactory.log();
        
      return response.data;
    });
  };

  this.submitForm = function (oService, oServiceData, formData) {
    var oFuncNote = {sHead:"Сабміт форми послуги", sFunc:"submitForm"};
    ErrorsFactory.init(oFuncNote);
    var nID_Server = oServiceData.nID_Server;
    //--//var url = oServiceData.sURL + oServiceData.oData.sPath;
    var data = prepareFormData(oService, oServiceData, formData, nID_Server);//url
    return $http.post('./api/process-form', data).then(function (response) {
        //{"processDefinitionId":"cnap_293:1:1"}
        var asParam=['nID_Service: '+oService.nID, 'nID_ServiceData: '+oServiceData.nID, 'processDefinitionId: '+oServiceData.oData.processDefinitionId, "data: "+data];
        if(ErrorsFactory.bSuccessResponse(response.data,function(sResponseMessage){
            if (sResponseMessage && sResponseMessage.indexOf('happened when sending email') > -1) {
                return {sType: "error", sBody: 'Помилка при відсилці єлектронної пошти! (скоріш за все не вірні дані вказані у формі чи електроний адрес)', asParam:asParam};
            } else if (sResponseMessage && sResponseMessage.indexOf('Exception while invoking TaskListener') > -1) {
                return {sType: "error", sBody: 'Помилка при обробці листенера! (скоріш за все не вірні дані вказані у формі)', asParam:asParam};
            } else if (sResponseMessage && sResponseMessage.indexOf("For input string") > -1) {
                return {sType: "error", sBody: 'Помилка при обробці строкового поля форми! (скоріш за все не вірні дані вказані у формі)', asParam:asParam};
            } else if (sResponseMessage && sResponseMessage.indexOf("Invalid value for") > -1) {
                return {sType: "error", sBody: 'Помилка при обробці значення поля форми! (скоріш за все не вірні дані вказані у формі)', asParam:asParam};
            } else if (sResponseMessage) {
                return {sType: "error", sBody: 'Помилка при запросі! (перевірте введені дані)', asParam:asParam};
            } else {
                return {sType: "error", sBody: 'Невідома помилка при запросі! (перевірте введені дані)', asParam:asParam};
            }                    
        })){
            
        }
        ErrorsFactory.log();
        
      /*if (/err/i.test(response.data.code)) {
          //ErrorsFactory.addFail({""})
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }*/
      return response.data;
    });
  };

  this.loadForm = function (oServiceData, formID) {
    var oFuncNote = {sHead:"Завантаженя форми послуги", sFunc:"loadForm"};
    ErrorsFactory.init(oFuncNote);
    //--//var data = {sURL: oServiceData.sURL, formID: formID};
    var data = {nID_Server: oServiceData.nID_Server, formID: formID};

    return $http.get('./api/process-form/load', {params: data}).then(function (response) {
        var asParam=['nID_ServiceData: '+oServiceData.nID, 'formID: '+formID];
        if(ErrorsFactory.bSuccessResponse(response.data,function(sResponseMessage){
            if (sResponseMessage) {
                return {sType: "error", sBody: 'Помилка при запросі!', asParam:asParam};
            } else {
                return {sType: "error", sBody: 'Невідома помилка при запросі!', asParam:asParam};
            }                    
        })){
        }
        ErrorsFactory.log();        
      /*if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }*/
      return response.data;
    });
  };

  this.saveForm = function (oService, oServiceData, businessKey, processName, activitiForm, formData) {
    var oFuncNote = {sHead:"Збереження форми послуги", sFunc:"saveForm"};
    ErrorsFactory.init(oFuncNote);
    //var url = oServiceData.sURL + oServiceData.oData.sPath;
    var nID_Server = oServiceData.nID_Server;
    var oFormData = prepareFormData(oService, oServiceData, formData, nID_Server);
    var data = {
      formData : oFormData,//url
      activitiForm: activitiForm,
      processName : processName,
      businessKey : businessKey
    };

    var restoreFormUrl = $location.absUrl();

    var params = {
      //--//sURL : oServiceData.sURL
      nID_Server : nID_Server
    };
    data = angular.extend(data, {
      restoreFormUrl: restoreFormUrl
    });

    return $http.post('./api/process-form/save', data, {params : params}).then(function (response) {
        var asParam=['nID_Service: '+oService.nID, 'nID_ServiceData: '+oServiceData.nID, 'processName: '+processName, 'businessKey: '+businessKey, 'oFormData: '+oFormData];
        if(ErrorsFactory.bSuccessResponse(response.data,function(sResponseMessage){
            if (sResponseMessage) {
                return {sType: "error", sBody: 'Помилка при запросі!', asParam:asParam};
            } else {
                return {sType: "error", sBody: 'Невідома помилка при запросі!', asParam:asParam};
            }                    
        })){
        }
        ErrorsFactory.log();        
        
      /*if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }*/
      return response.data;
    });
  };

  this.getSignFormPath = function (oServiceData, formID, oService) {
    //return '/api/process-form/sign?formID=' + formID + '&sURL=' + oServiceData.sURL;
    //--//return '/api/process-form/sign?formID=' + formID + '&sURL=' + oServiceData.sURL + '&sName=' + oService.sName;
    return '/api/process-form/sign?formID=' + formID + '&nID_Server=' + oServiceData.nID_Server + '&sName=' + oService.sName;

  };

  this.getUploadFileURL = function (oServiceData) {
    //--//return './api/uploadfile?url=' + oServiceData.sURL + 'service/object/file/upload_file_to_redis';
    return './api/uploadfile?nID_Server=' + oServiceData.nID_Server;
  };

  this.updateFileField = function (oServiceData, formData, propertyID, fileUUID) {
    formData.params[propertyID].value = fileUUID;
  };

  this.checkFileSign = function (oServiceData, fileID){
    var oFuncNote = {sHead:"Перевірка ЕЦП у файлу", sFunc:"checkFileSign"};
    ErrorsFactory.init(oFuncNote);
    var asParam=['nID_ServiceData: '+oServiceData.nID, 'fileID: '+fileID];
    return $http.get('./api/process-form/sign/check', {
      params : {
        fileID : fileID,
        //--//sURL : oServiceData.sURL
        nID_Server : oServiceData.nID_Server
      }
    }).then(function (response) {
        if(ErrorsFactory.bSuccessResponse(response.data,function(sResponseMessage){
            if (sResponseMessage) {
                return {sType: "error", sBody: 'Раптова помилка при запросі!', asParam:asParam};
            } else {
                return {sType: "error", sBody: 'Раптова невідома помилка при запросі!', asParam:asParam};
            }                    
        })){
        }
        ErrorsFactory.log();        
        return response.data;
    }).catch(function (error) {
        if(ErrorsFactory.bSuccessResponse(error.data,function(sResponseMessage){
            if (sResponseMessage) {
                return {sType: "error", sBody: 'Помилка при запросі!', asParam:asParam};
            } else {
                return {sType: "error", sBody: 'Невідома помилка при запросі!', asParam:asParam};
            }                    
        })){
        }
        ErrorsFactory.log();        
      /*ErrorsFactory.push({
        type: "danger",
        text: [error.data.code, error.data.message].join(" ")
      });*/
      return $q.reject(error.data);
    });
  };

  this.autoUploadScans = function (oServiceData, scans) {
    var oFuncNote = {sHead:"Завантаженя файлу", sFunc:"autoUploadScans"};
    ErrorsFactory.init(oFuncNote);
    var data = {
      //--//url: oServiceData.sURL + 'service/object/file/upload_file_to_redis',
      nID_Server: oServiceData.nID_Server,
      scanFields: scans
    };

    return $http.post('./api/process-form/scansUpload', data).then(function (response) {
        var asParam=['nID_ServiceData: '+oServiceData.nID, 'scans: '+scans];
        if(ErrorsFactory.bSuccessResponse(response.data,function(sResponseMessage){
            if (sResponseMessage) {
                return {sType: "error", sBody: 'Помилка при запросі!', asParam:asParam};
            } else {
                return {sType: "error", sBody: 'Невідома помилка при запросі!', asParam:asParam};
            }                    
        })){
        }
        ErrorsFactory.log();        
        
      /*if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }*/
      return response.data;
    });
  }
});
