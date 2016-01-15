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
      return response.data;
    });
  };

  this.submitForm = function (oService, oServiceData, formData) {
    var nID_Server = oServiceData.nID_Server;
    //--//var url = oServiceData.sURL + oServiceData.oData.sPath;
    var data = prepareFormData(oService, oServiceData, formData, nID_Server);//url

    return $http.post('./api/process-form', data).then(function (response) {
      if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }
      return response.data;
    });
  };

  this.loadForm = function (oServiceData, formID) {
    //--//var data = {sURL: oServiceData.sURL, formID: formID};
    var data = {nID_Server: oServiceData.nID_Server, formID: formID};

    return $http.get('./api/process-form/load', {params: data}).then(function (response) {
      if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }
      return response.data;
    });
  };

  this.saveForm = function (oService, oServiceData, businessKey, processName, activitiForm, formData) {
    //var url = oServiceData.sURL + oServiceData.oData.sPath;
    var nID_Server = oServiceData.nID_Server;
    var data = {
      formData : prepareFormData(oService, oServiceData, formData, nID_Server),//url
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
      if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }
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
    return './api/uploadfile?nID_Server=' + oServiceData.nID_Server + 'service/object/file/upload_file_to_redis';
  };

  this.updateFileField = function (oServiceData, formData, propertyID, fileUUID) {
    formData.params[propertyID].value = fileUUID;
  };

  this.checkFileSign = function (oServiceData, fileID){
    return $http.get('./api/process-form/sign/check', {
      params : {
        fileID : fileID,
        //--//sURL : oServiceData.sURL
        nID_Server : oServiceData.nID_Server
      }
    }).then(function (response) {
        return response.data;
    }).catch(function (error) {
      ErrorsFactory.push({
        type: "danger",
        text: [error.data.code, error.data.message].join(" ")
      });
      return $q.reject(error.data);
    });
  };

  this.autoUploadScans = function (oServiceData, scans) {
    var data = {
      //--//url: oServiceData.sURL + 'service/object/file/upload_file_to_redis',
      nID_Server: oServiceData.nID_Server,
      scanFields: scans
    };

    return $http.post('./api/process-form/scansUpload', data).then(function (response) {
      if (/err/i.test(response.data.code)) {
        ErrorsFactory.push({
          type: "danger",
          text: [response.data.code, response.data.message].join(" ")
        });
      }
      return response.data;
    });
  }
});
