angular.module('app').service('ActivitiService', function ($q, $http, $location, ErrorsFactory, $filter, generationService, uiUploader, $rootScope) {

  var aFieldFormData = function (aFormProperties,formData) {//activitiForm
    var aField=[];
    //var aFormProperties = activitiForm.formProperties;
    if(aFormProperties && aFormProperties!==null){
        angular.forEach(aFormProperties, function(oProperty){
            if(oProperty.type==="string" || oProperty.type==="enum" || oProperty.type==="long" || oProperty.type==="date" || oProperty.type==="textArea" || oProperty.type==="queueData" || oProperty.type==="select" || oProperty.type==="file"){
                //var oField = {sID:oProperty.id,sType:oProperty.type,sValue:oProperty.value};
                var oField = {sID:oProperty.id,sType:oProperty.type,sValue:formData.params[oProperty.id].value};//oProperty.value
                //formData.params[propertyID].value
                aField = aField.concat([oField]);
            }
//                console.log("oProperty.id="+oProperty.id+",oProperty.type="+oProperty.type+",oProperty.bVariable="+oProperty.bVariable);
            //oProperty.enumValues = a;
            //if(oProperty.type === "enum" && oProperty.enumValues && oProperty.enumValues != null && oProperty.enumValues.length == 0){//oProperty.id === attr.sName &&
//            if(oProperty.type === "enum" && oProperty.bVariable && oProperty.bVariable !== null && oProperty.bVariable === true){//oProperty.id === attr.sName &&
//                    console.log('oProperty.type === "enum" && oProperty.enumValues && oProperty.enumValues != null && oProperty.enumValues.length == 0');
//                $scope.data.formData.params[oProperty.id].value=null;
//            }
        });
    }
    return aField;
  };

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
      nID_ServiceData: oServiceData.nID,
      nID_Region : nID_Region,
      sID_UA : sID_UA,
      sID_UA_Common : sID_UA_Common
    };

    data = angular.extend(data, formData.getRequestObject(), {contentToSign: formData.contentToSign});
    data = angular.extend(data, params);

    return data;
  };

  this.getForm = function (oServiceData, processDefinitionId) {
    var oFuncNote = {sHead:"Отримання форми послуги", sFunc:"getForm"};
    var oData = {
      'nID_Server': oServiceData.nID_Server
      , 'sID_BP_Versioned': processDefinitionId.sProcessDefinitionKeyWithVersion
    };
    ErrorsFactory.init(oFuncNote, {asParam:['nID_ServiceData: '+oServiceData.nID, 'sID_BP_Versioned: '+oData.sID_BP_Versioned]});
    return $http.get('./api/process-form', {
      params: oData,
      data: oData
    }).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse.data)){
            return oResponse.data;
        }
    });
  };

  this.submitForm = function (oService, oServiceData, formData, aFormProperties) {//activitiForm
    var oFuncNote = {sHead:"Сабміт форми послуги", sFunc:"submitForm"};
    var aField = aFieldFormData(aFormProperties,formData);//activitiForm
    ErrorsFactory.init(oFuncNote, {asParam: ['nID_Service: '+oService.nID, 'nID_ServiceData: '+oServiceData.nID, 'processDefinitionId: '+oServiceData.oData.processDefinitionId, "saField: "+JSON.stringify(aField)]});
    var nID_Server = oServiceData.nID_Server;
    var oFormData = prepareFormData(oService, oServiceData, formData, nID_Server);
    return $http.post('./api/process-form', oFormData).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse.data,function(oThis, doMerge, sMessage, aCode, sResponse){
//            console.log("[submitForm]sMessage="+sMessage+",aCode="+aCode+",sResponse="+sResponse);
            if (!sMessage) {
            } else if (sMessage.indexOf(['happened when sending email']) > -1) {
                doMerge(oThis, {sBody: 'Помилка відсилки єлектронної пошти! (скоріш за все не вірні дані вказані у формі чи електроний адрес)'});
            } else if (sMessage.indexOf(['Exception while invoking TaskListener']) > -1) {
                doMerge(oThis, {sBody: 'Помилка роботи листенера! (скоріш за все не вірні дані вказані у формі)'});
            } else if (sMessage.indexOf(["For input string"]) > -1) {
                doMerge(oThis, {sBody: 'Помилка обробки строкового поля форми! (скоріш за все не вірні дані вказані у формі)'});
            } else if (sMessage.indexOf(["Invalid value for"]) > -1) {
                doMerge(oThis, {sBody: 'Помилка обробки значення поля форми! (скоріш за все не вірні дані вказані у формі)'});
            }
        })){
//            console.log("[submitForm](OK)oResponse.data="+JSON.stringify(oResponse.data));
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
        if(ErrorsFactory.bSuccessResponse(oResponse.data)){
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
    var oFormDataPrintPdf = angular.copy(oFormData);
    angular.forEach(activitiForm.formProperties, function (formProperty) {
      if(formProperty.type === 'enum') {
        if(formProperty.id in oFormDataPrintPdf.params) {
          angular.forEach(formProperty.enumValues, function (enumValue) {
            if(enumValue.id === oFormDataPrintPdf.params[formProperty.id]) {
              oFormDataPrintPdf.params[formProperty.id] = enumValue.name;
            }
          })
        }
      }
    });
    var aField = aFieldFormData(activitiForm.formProperties,formData);//activitiForm
    ErrorsFactory.init(oFuncNote, {asParam: ['nID_Service: '+oService.nID, 'nID_ServiceData: '+oServiceData.nID, 'processName: '+processName, 'businessKey: '+businessKey, 'saField: '+JSON.stringify(aField)]});
    var oData = {
      formData : oFormData,
      formDataPrintPdf : oFormDataPrintPdf,
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
        if(ErrorsFactory.bSuccessResponse(oResponse.data)){
          if(oResponse.data.formDataPrintPdf){
            delete oResponse.data.formDataPrintPdf;
          }
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

  this.getSignFormPath = function (oServiceData, formID, oService, formDataParams, bConvertToPDF) {
    var formKey, isNewService;
    if(formID && formID.indexOf('sKey') > -1) {
      try {
        formKey = JSON.parse(formID).sKey;
      } catch (e) {}
    }
    var path = formKey ? formKey : formID;
    if(formDataParams.hasOwnProperty('form_signed_1')){
      isNewService = formDataParams['form_signed_1'].newAttach ? '&isNew=true' : '';
      return '/api/process-form/sign?formID=' + path + '&nID_Server=' + oServiceData.nID_Server + '&sName=' + oService.sName + '&bConvertToPDF=' + bConvertToPDF + isNewService;
    } else if (formDataParams.hasOwnProperty('form_signed_all')) {
      isNewService = formDataParams['form_signed_all'].newAttach ? '&isNew=true' : '';
      return '/api/process-form/signMultiple?formID=' + path + '&nID_Server=' + oServiceData.nID_Server + '&sName=' + oService.sName + isNewService;
    }
  };

  this.getUploadFileURL = function (oServiceData, customFileName, file) {
    if(file){
      return this.getUploadFileURLByServer(oServiceData.nID_Server, null, file);
    } else {
      return this.getUploadFileURLByServer(oServiceData.nID_Server, customFileName);
    }
  };

  this.getUploadFileURLByServer = function (nID_Server, customFileName, file) {
    if(file) {
      return './api/uploadfile?sFileNameAndExt=' + file.name + (file.id ? '&sID_Field=' + file.id : '');
    } else {
      return './api/uploadfile?nID_Server=' + nID_Server + (customFileName ? 'customFileName=' + customFileName : '');
    }
  };

  this.updateFileField = function (oServiceData, formData, propertyID, fileUUID) {
    formData.params[propertyID].value = fileUUID;
  };

  this.checkFileSign = function (oServiceData, fileID){

    try{
      var keyOrJSON = JSON.parse(fileID);
      if(typeof keyOrJSON === 'object' && 'sKey' in keyOrJSON) {
        fileID = JSON.parse(fileID).sKey;
      }
    } catch(e){}

    var oFuncNote = {sHead:"Перевірка ЕЦП у файлу", sFunc:"checkFileSign"};
    ErrorsFactory.init(oFuncNote, {asParam: ['nID_ServiceData: '+oServiceData.nID, 'fileID: '+fileID]});
    return $http.get('./api/process-form/sign/check', {
      params : {
        fileID : fileID,
        nID_Server : oServiceData.nID_Server
      }
    }).then(function (oResponse) {
        if(oResponse.data) {
          return oResponse.data;
        }
        if(ErrorsFactory.bSuccessResponse(oResponse.data)){
            return oResponse.data;
        }
    }).catch(function (error) {
        if(!ErrorsFactory.bSuccessResponse(error.data)){
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
      scanFields: scans
    };
    return $http.post('./api/process-form/scansUpload?nID_Server=' + oServiceData.nID_Server, oData).then(function (oResponse) {
        if(ErrorsFactory.bSuccessResponse(oResponse.data)){
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

  /**
   * Ф-ция загрузки fileHTML как аттачей
   * @param formProperties
   * @param form
   * @returns {deferred.promise|{then, always}}
   */
  this.uploadFileHTML = function (formProperties, form) {
    var htmls = [],
        htmlPromises = [],
        htmlDefer = [],
        counter = 0,
        deferred = $q.defer();

    var filesFields = form.filter(function (prop) {
      return prop.type === 'fileHTML';
    });

    angular.forEach(filesFields, function (file, key) {
      var data = {
        sFileNameAndExt: file.id + '.html',
        sID_Field: file.id,
        sContent: formProperties[file.id].value,
        sID_StorageType: 'Redis'
      };

      htmlDefer[key] = $q.defer();
      htmls[key] = {html:data.sContent, data:data};
      htmlPromises[key] = htmlDefer[key].promise;
    });

    var htmlUpload = function (i, html, defs) {
      if (i < html.length) {
        return $http.post('api/uploadfile/uploadFileHTML', html[i].data)
          .then(function (uploadResult) {
            if(formProperties[htmls[i].data.sID_Field] && formProperties[htmls[i].data.sID_Field].value){
              formProperties[htmls[i].data.sID_Field].value = uploadResult.data;
            }
            defs[i].resolve();
            return htmlUpload(i+1, html, defs);
          });
      }
    };

    htmlUpload(counter, htmls, htmlDefer);

    $q.all(htmlPromises).then(function () {
      deferred.resolve();
    });

    return deferred.promise;
  };

  this.getPatternFile = function (sPathFile, serverID) {
    return $http.get('api/process-form/loadPatternFile',{
      params: {
        sPathFile: sPathFile,
        sServerId: serverID
      }
    }).then(function (response) {
      if (response.data) {
        return response.data;
      }
    });
  };

  this.generatePDFFromPrintForms = function (activitiFormProperties, oServiceData, formData, attaches) {
    var self = this,
      deferred = $q.defer(),
      filesDefers = [],
      resultsPdf = [];

    // нужно найти все поля с типом "string" и id, начинающимся с "PrintForm_"
    var filesFields = $filter('filter')(activitiFormProperties, function (prop) {
      return prop.type === 'string' && /^PrintFormAutoSign_/.test(prop.id);
    });

    // загрузить все шаблоны
    angular.forEach(filesFields, function (fileField) {
      var defer = $q.defer();
      filesDefers.push(defer.promise);
      var patternFileName = fileField.value;
      if (patternFileName) {
        patternFileName = patternFileName.replace(/^pattern\//, '');
        self.getPatternFile(patternFileName, oServiceData.nID_Server).then(function (result) {
          defer.resolve({
            fileField: fileField,
            fileBase64: null,
            template: result
          });
        });
      } else
        defer.resolve({
          fileField: fileField,
          fileBase64: null,
          template: ''
        });
    });
    // компиляция и отправка html
    $q.all(filesDefers).then(function (results) {
      var aPrintDefer = [],
        aPrintPromise = [],
        aPrintform = [],
        counter = 0;

      angular.forEach(results, function (templateResult, key) {
        if(!templateResult.fileBase64){
          for (var prop in formData) {
            if (formData.hasOwnProperty(prop)) {
              templateResult.template = templateResult.template.split('[' + prop + ']').join(formData[prop].value);
            }
          }

          function getUserInfo() {
            var sUserInfo = '';
            if(formData){
              if(formData.bankIdlastName){
                sUserInfo = sUserInfo + formData.bankIdlastName.value;
              }
              if(formData.bankIdfirstName){
                if(sUserInfo !== ''){
                  sUserInfo = sUserInfo + ' ';
                }
                sUserInfo = sUserInfo + formData.bankIdfirstName.value;
              }
              if(formData.bankIdmiddleName){
                if(sUserInfo !== ''){
                  sUserInfo = sUserInfo + ' ';
                }
                sUserInfo = sUserInfo + formData.bankIdmiddleName.value;
              }
            }
            return sUserInfo;
          }

          var dateCreate = new Date();
          var formatedDateCreate = dateCreate.getFullYear() + '-' + ('0' + (dateCreate.getMonth() + 1)).slice(-2) + '-' + ('0' + dateCreate.getDate()).slice(-2);
          var formatedTimeCreate = ('0' + dateCreate.getHours()).slice(-2) + ':' + ('0' + dateCreate.getMinutes()).slice(-2);
          var userInformation = getUserInfo();
          templateResult.template = templateResult.template.split('[sDateCreateProcess]').join(formatedDateCreate);
          templateResult.template = templateResult.template.split('[sTimeCreateProcess]').join(formatedTimeCreate);
          templateResult.template = templateResult.template.split('[sDateTimeCreateProcess]').join(formatedDateCreate + ' ' + formatedTimeCreate);
          templateResult.template = templateResult.template.split('[sDateCreate]').join(formatedDateCreate + ' ' + formatedTimeCreate);
          templateResult.template = templateResult.template.split('[sCurrentDateTime]').join(formatedDateCreate + ' ' + formatedTimeCreate);
          templateResult.template = templateResult.template.split('[sUserInfo]').join(userInformation);

          aPrintDefer[key] = $q.defer();
          aPrintform[key] = templateResult;
          aPrintPromise[key] = aPrintDefer[key].promise;
        }

      });

      var asyncPdfGenerate = function (i, print, defs) {
        if (i < print.length) {
          var printContents = print[i].template;
          if(printContents){
            return generationService.generatePDFFromHTML(printContents).then(function (pdfContent) {
              resultsPdf.push({
                id: print[i].fileField.id,
                content: pdfContent.base64
              });
              defs[i].resolve();
              return asyncPdfGenerate(i + 1, print, defs);
            })
          } else {
            defs[i].resolve();
            return asyncPdfGenerate(i + 1, print, defs);
          }
        }
      };

      asyncPdfGenerate(counter, aPrintform, aPrintDefer);

      $q.all(aPrintPromise).then(function () {
        if (attaches) {
          self.convertFilesToPdf(attaches).then(function (res) {
            var result = res.concat(resultsPdf);
            deferred.resolve(result);
          });
        } else {
          deferred.resolve(resultsPdf);
        }
      });

    });

    return deferred.promise;
  };

  this.upload = function(files, oServiceData, fieldId) {
    var deferred = $q.defer();
    var self = this;
    var scope = $rootScope.$new(true, $rootScope);
    var url = '/api/uploadfile?sFileNameAndExt=' + files[0].name;

    uiUploader.removeAll();
    uiUploader.addFiles(files);
    uiUploader.startUpload({
      url: url,
      concurrency: 1,
      onProgress: function (file) {
        scope.$apply(function () {

        });
      },
      onCompleted: function (file, response) {
        try {
          var param = JSON.parse(response);
          $http.get('./api/process-form/sign/check', {
            params : {
              fileID : param.sKey,
              nID_Server : oServiceData.nID_Server
            }
          }).then(function (oResponse) {
            if(oResponse.data) {
              deferred.resolve({file: file, response: param, signInfo: oResponse.data});
            }
            if(ErrorsFactory.bSuccessResponse(oResponse.data)){
              deferred.resolve({file: file, response: param, signInfo: oResponse.data});
            }
          }).catch(function (error) {
            if(!ErrorsFactory.bSuccessResponse(error.data)){
              deferred.reject(error.data);
            }
          });
        } catch (e) {
          console.error(e);
        }
      }
    });

    return deferred.promise;
  };

  this.uploadAttachments = function (aContents, formData, formProperties, oServiceData) {
    var deferred = $q.defer();
    var self = this;

    var contents = [],
      documentPromises = [],
      docDefer = [],
      counter = 0;

    angular.forEach(aContents, function (oContent, key) {
      docDefer[key] = $q.defer();
      contents[key] = oContent;
      documentPromises[key] = docDefer[key].promise;
    });

    var uploadingResult = [];

    var asyncUpload = function (i, docs, defs) {
      if (i < docs.length) {

        return self.uploadAttachToForm(docs[i], formData, formProperties, oServiceData).then(function (resp) {
          uploadingResult.push(resp);
          defs[i].resolve(resp);
          return asyncUpload(i + 1, docs, defs);
        }, function (err) {
          uploadingResult.push({error : err});
          defs[i].reject(err);
          return asyncUpload(i + 1, docs, defs);
        });

      }
    };

    asyncUpload(counter, contents, docDefer);

    $q.all(documentPromises).then(function () {
      deferred.resolve(uploadingResult);
    });

    return deferred.promise;
  };

  this.uploadAttachToForm = function (content, formData, formProperties, oServiceData) {
    var deferred = $q.defer();

    this.upload(content.files, oServiceData, content.fieldId).then(function (result) {
      var postfix = content && content.fieldId && content.fieldId.indexOf('PrintFormAutoSign') === 0 ? content.fieldId.split('_') : '',
        isEqual = false;
      if (postfix.length === 2) {
        postfix = postfix[1];
      }

      for (var field in formData) {
        if (formData.hasOwnProperty(field)) {
          if (field === ('form_signed_' + postfix) || field === ('form_signed_all_' + postfix) || content.fieldId === field) {
            formData[field].value = JSON.stringify(result.response);
            formData[field].fileName = result.file.sFileNameAndExt;
            formData[field].signInfo = result.eds;
            isEqual = true;
            break;
          }
        }
      }

      if (!isEqual) {
        for (var j = 0; j < formProperties.length; j++) {
          if (formProperties[j].type === 'table') {
            for (var c = 0; c < formProperties[j].aRow.length; c++) {
              var row = formProperties[j].aRow[c];
              for (var i = 0; i < row.aField.length; i++) {
                if (row.aField[i].id === content.fieldId) {
                  formProperties[j].aRow[c].aField[i].value = {
                    id: JSON.stringify(result.response),
                    fromDocuments: false,
                    signInfo: result.eds
                  };
                  break
                }
              }
            }
          }
        }
      }

      deferred.resolve(result);
    }, function (err) {

      deferred.reject(err);
    });

    return deferred.promise;
  };

  this.convertFilesToBase64 = function (filesForConvert) {
    var defer = $q.defer(),
      objectOfDefers = {},
      objectOfPromises = {},
      aContentData = [];

    angular.forEach(filesForConvert, function (uf, key) {
      objectOfDefers[key] = $q.defer();
      objectOfPromises[key] = objectOfDefers[key].promise;
      var reader = new FileReader();
      reader.onload = function (e) {
        var loadedContent = e.target.result.split("base64,")[1];
        var id = uf[0].name;
        aContentData.push({id: key, content: loadedContent, base64encoded: true, fieldId: id});
        objectOfDefers[key].resolve();
      };
      reader.readAsDataURL(uf[0]);
    });

    $q.all(objectOfPromises).then(function () {
      defer.resolve(aContentData);
    });

    return defer.promise;
  };

  this.convertFilesToPdf = function (files) {
    var deferred = $q.defer();
    var resultsPdf = [];

    this.convertFilesToBase64(files).then(function (convertedFiles) {
      var deferArray = [];
      var deferPromises = [];
      var counter = 0;

      for (var i=0; i<convertedFiles.length; i++) {
        deferArray[i] = $q.defer();
        deferPromises[i] = deferArray[i].promise;
      }

      var asyncPdfGenerate = function (i, print, defs) {
        if (i < print.length) {
          var printContents = print[i].content;
          var nameWithExt = print[i].fieldId ? print[i].fieldId.split('.') : null;
          if(printContents && nameWithExt && nameWithExt.indexOf('pdf') !== nameWithExt.length - 1){
            return generationService.generatePDFFromHTML(printContents).then(function (pdfContent) {
              resultsPdf.push({
                id: print[i].id,
                name: print[i].fieldId,
                content: pdfContent.base64
              });
              deferArray[i].resolve();
              return asyncPdfGenerate(i + 1, print, defs);
            })
          } else if(nameWithExt && nameWithExt.indexOf('pdf') === nameWithExt.length - 1) {
            resultsPdf.push({
              id: print[i].id,
              name: print[i].fieldId,
              content: print[i].content
            });
            deferArray[i].resolve();
            return asyncPdfGenerate(i + 1, print, defs);
          } else {
            deferArray[i].resolve();
            return asyncPdfGenerate(i + 1, print, defs);
          }
        }
      };

      asyncPdfGenerate(counter, convertedFiles, deferArray);
      $q.all(deferPromises).then(function () {
        deferred.resolve(resultsPdf);
      });
    });
    return deferred.promise;
  };
});
