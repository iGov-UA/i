var url = require('url')
  , request = require('request')
  , fs = require('fs')
  , FormData = require('form-data')
  , config = require('../../config/environment')
  , authProviderRegistry = require('../../auth/auth.provider.registry')
  , _ = require('lodash')
  , StringDecoder = require('string_decoder').StringDecoder
  , async = require('async')
  , formTemplate = require('./form.template')
  , uploadFileService = require('../uploadfile/uploadfile.service')
  , formService = require('./form.service')
  , activiti = require('../../components/activiti')
  , admZip = require('adm-zip')
  , errors = require('../../components/errors')
  , loggerFactory = require('../../components/logger')
  , logger = loggerFactory.createLogger(module);

var createError = function (error, error_description, response) {
  return {
    code: response ? response.statusCode : 500,
    err: {
      error: error,
      error_description: error_description
    }
  };
};

module.exports.index = function (req, res) {
  var sHost = req.region.sHost;
  var sID_BP_Versioned = req.query.sID_BP_Versioned;

  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };

  activiti.get('/service/form/form-data', {processDefinitionId: sID_BP_Versioned}, callback, sHost);
};

module.exports.submit = function (req, res) {
  var formData = req.body;
  var nID_Subject = req.session.subject.nID;
  var sHost = req.region.sHost;
  var keys = [];
  var properties = [];

  function formSubmit() {
    for (var id in formData.params) {
      if (formData.params.hasOwnProperty(id)) {
        var value = formData.params[id];
        if (id === 'nID_Subject') {
          value = nID_Subject;
        }
        if (id === 'sID_UA' && formData.sID_UA_Common !== null) {
          value = formData.sID_UA_Common;
        } else if (id === 'sID_UA') {
          value = formData.sID_UA;
        }

        properties.push({
          id: id,
          value: value
        });
      }
    }

    var callback = function (error, response, body) {
      res.send(body);
      res.end();
    };

    var qs = {
      nID_Subject: nID_Subject,
      nID_Service: formData.nID_Service,
      nID_ServiceData: formData.nID_ServiceData,
      nID_Region: formData.nID_Region,
      sID_UA: formData.sID_UA
    };

    var body = {
      processDefinitionId: formData.processDefinitionId,
      businessKey: "key",
      nID_Subject: nID_Subject,
      properties: properties
    };

    activiti.post('/service/form/form-data', qs, body, callback, sHost);
  }

  for (var key in formData.params) {
    if (formData.params[key] !== null && typeof formData.params[key] === 'object') {
      keys.push(key);
    }
  }

  if (keys.length > 0) {
    async.forEach(keys, function (key, next) {
        function putTableToRedis (table, callback) {
          var url,
              params = {},
              nameAndExt = table.id + '.json',
              checkForNewService = table.name.split(';');

          // now we have two services for saving table, so we checking what service is needed;
          if(checkForNewService.length === 3 && checkForNewService[2].indexOf('bNew=true') > -1) {
            url = '/object/file/setProcessAttach';
            params = {
              sID_StorageType:'Redis',
              sID_Field:table.id,
              sFileNameAndExt:nameAndExt
            }
          } else {
            url = '/object/file/upload_file_to_redis';
          }

          activiti.upload(url, params, nameAndExt, JSON.stringify(table), callback);
        }

        putTableToRedis(formData.params[key], function (error, response, data) {
          formData.params[key] = data;
          next()
        })
      },
      function (err) {
        formSubmit();
      });
  } else {
    formSubmit();
  }
};

module.exports.scanUpload = function (req, res) {
  var sHost = req.region.sHost;
  var accessToken = req.session.access.accessToken;
  var data = req.body;
  var sURL = sHost + '/service/object/file/upload_file_to_redis';
  var sURLNew = sHost + '/service/object/file/setProcessAttach';
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);
  console.log("[scanUpload]:sURL=" + sURL);
  var uploadURL = sURL; //data.url
  var uploadNewURL = sURLNew;
  var documentScans = data.scanFields;
  console.log("[scanUpload]:data.scanFields=" + data.scanFields);

  if (!userService.scanContentRequest) {
    res.status(400).send(errors.createError(errors.codes.LOGIC_SERVICE_ERROR,
      'type of authorization doesn\'t support documents and scan-copies'));
    return;
  }

  var uploadResults = [];
  var uploadScan = function (documentScan, callback) {
    userService.scanContentRequest(documentScan.scan.type, documentScan.scan.link, accessToken, function (error, buffer) {
      if (error) {
        callback(error);
      } else {
        var form = new FormData();
        form.append('file', buffer, {
          filename: documentScan.scan.type + '.' + documentScan.scan.extension
        });

        var requestOptionsForUploadContent = {
          url: documentScan.isNew ? uploadNewURL : uploadURL,
          auth: getAuth(),
          headers: form.getHeaders()
        };

        if(documentScan.isNew) {
          requestOptionsForUploadContent.qs = {
            sFileNameAndExt: documentScan.scan.type + '.' + documentScan.scan.extension,
            sID_StorageType : 'Redis'
          }
        }

        pipeFormDataToRequest(form, requestOptionsForUploadContent, function (error, result) {
          logger.info('[scanUpload]: scan redis id ', {redisanswer: result, error : error});
          if(error){
            callback(error);
          } else {
            uploadResults.push({
              fileID: result.data,
              scanField: documentScan
            });
            callback();
          }
        });
      }
    });
  };

  async.forEach(documentScans, function (documentScan, callback) {
    uploadScan(documentScan, callback);
  }, function (error) {
    if (error) {
      res.status(500).send(error);
    } else {
      res.send(uploadResults);
    }
  });

};

module.exports.signCheck = function (req, res) {
  var fileID = req.query.fileID;
  var sHost = req.region.sHost;

  var sURL = sHost + '/';
  console.log("sURL=" + sURL);

  if (!fileID) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, 'fileID should be specified'));
    return;
  }

  if (!sURL) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, 'sURL should be specified'));
    return;
  }

  var reqParams = activiti.buildRequest(req, 'service/object/file/check_file_from_redis_sign', {
    sID_File_Redis: fileID
  }, sURL);
  _.extend(reqParams, {json: true});

  request(reqParams, function (error, response, body) {
    if (error) {
      error = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, 'Error while checking file\'s sign', error);
      res.status(500).send(error);
      return;
    }

    if (body.code && body.code === 'SYSTEM_ERR') {
      error = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, body.message, body);
      res.status(500).send(error);
      return;
    }

    if (body.customer && body.customer.signatureData) {
      res.status(200).send(body.customer.signatureData);
    } else {
      res.status(200).send({});
    }
  });
};

module.exports.signFormMultiple = function (req, res) {
  var oServiceDataNID = req.query.oServiceDataNID;
  var sName = req.query.sName;
  var nID_Server = req.query.nID_Server;
  var formID = req.session.formID;
  var sHost = req.region.sHost;
  var sURL = sHost + '/';
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);
  var newService = '';
  var storageType = 'Redis';

  if (!formID) {
    res.status(400).send({error: 'formID should be specified'});
  }

  if (!oServiceDataNID && !sURL) {
    res.status(400).send({error: 'Either sURL or oServiceDataNID should be specified'});
    return;
  }

  if(req.query.isNew) {
    var isNewService = JSON.parse(req.query.isNew);
    newService = '&isNew=' + isNewService;
  }

  var callbackURL = url.resolve(originalURL(req, {}), '/api/process-form/signMultiple/callback?nID_Server=' + nID_Server + newService);

  function findFileFields(formData) {
    var tableFiles = [];
    var fileFields = formData.activitiForm.formProperties.filter(function (property) {
      return property.type === 'file';
    });

    var tables = formData.activitiForm.formProperties.filter(function (property) {
      return property.type === 'table';
    });

    fileFields.forEach(function (fileField) {
      if (formData.formData.params[fileField.id]) {
        fileField.value = formData.formData.params[fileField.id];
      }
    });

    tables.forEach(function (table) {
      if(formData.formData.params[table.id]) {
        if(table.aRow) {
          table.aRow.forEach(function (row) {
            row.aField.forEach(function (field) {
              if(field.type === 'file') {
                tableFiles.push(field);
              }
            })
          })
        }
      }
    });

    tableFiles.forEach(function (file) {
      if(file.value && file.value.id) {
        file.value = file.value.id;
      }
    });
    fileFields = fileFields.concat(tableFiles);

    fileFields = fileFields.filter(function (fileField) {
      return fileField.value;
    });

    return fileFields;
  }

  function createHtml(data, callback) {
    var formData = data.formData;

    var templateData = {
      formProperties: data.activitiForm.formProperties,
      processName: sName,
      businessKey: data.businessKey,
      creationDate: '' + new Date()
    };

    var patternFileName = null;

    templateData.formProperties.forEach(function (item) {
      var value = formData.params[item.id];
      if (value) {
        item.value = value;
      }
    });

    for (var key in formData.params) {
      if (formData.params.hasOwnProperty(key) && key.indexOf('PrintFormAutoSign_') === 0)
        patternFileName = formData.params[key];
    }

    if (patternFileName) {
      var reqParams = activiti.buildRequest(req, 'service/object/file/getPatternFile', {sPathFile: patternFileName.replace(/^pattern\//, '')}, sURL);
      request(reqParams, function (error, response, body) {
        for (var key in formData.params) {
          if (formData.params.hasOwnProperty(key)) {
            body = body.replace('[' + key + ']', formData.params[key]);
          }
        }
        callback(body);
      });
    } else {
      callback(formTemplate.createHtml(templateData));
    }
  }

  function getFormAsync(callbackAsync) {
    loadForm(formID, sURL, function (error, response, body) {
      if (error) {
        callbackAsync(error, null);
      } else {
        callbackAsync(null, {loadedForm: body});
      }
    });
  }

  var objectsToSign = [];

  function getHtmlAsync(result, callbackAsync) {
    createHtml(result.loadedForm, function (formToUpload) {
      objectsToSign.push({
        name: 'file',
        text: formToUpload,
        options: {
          filename: 'signedForm.html',
          contentType: 'text/html;charset=utf-8'
        }
      });
      callbackAsync(null, result);
    });
  }

  function getFileBuffersAsync(result, callbackAsync) {
    var formData = result.loadedForm;
    var filesToSign = [];
    async.forEach(findFileFields(formData), function (fileField, callbackEach) {
      var params = {};

      if(fileField.value && fileField.value.indexOf('sKey') > -1) {
        var obj = JSON.parse(fileField.value);
        var nameAndExtForTableFile = obj.sFileNameAndExt;
        fileField.value = obj.sKey;
      }
      params.ID = fileField.value;
      params.storageType = storageType;
      uploadFileService.downloadBuffer(params, function (error, response, buffer) {
        var fileName;
        if(!formData.formData.files[fileField.id] && nameAndExtForTableFile) {
          fileName = nameAndExtForTableFile;
        } else {
          var ext = formData.formData.files[fileField.id].split('.').pop().toLowerCase();
          fileName = fileField.id + (ext ? '.' + ext : '');
        }
        filesToSign.push({
          name: fileField.id,
          options: {
            filename: fileName
          },
          buffer: buffer
        });
        callbackEach();
      }, sHost)
    }, function (error) {
      if (error) {
        callbackAsync(error, null);
      } else {
        objectsToSign = objectsToSign.concat(filesToSign);
        callbackAsync(null, result);
      }
    });
  }

  function signFilesAsync(result, callbackAsync) {
    var accessToken = req.session.access.accessToken;
    userService.signFiles(accessToken, callbackURL, objectsToSign, false, function (error, signResult) {
      if (error) {
        callbackAsync(error, result);
      } else {
        result.signResult = signResult;
        callbackAsync(null, result)
      }
    });
  }

  async.waterfall([
    getFormAsync,
    getHtmlAsync,
    getFileBuffersAsync,
    signFilesAsync
  ], function (error, result) {
    if (error) {
      res.redirect(result.loadedForm.restoreFormUrl
        + '?formID=' + formID
        + '&error=' + JSON.stringify(error));
    } else {
      res.redirect(result.signResult.desc);
    }
  });
};

module.exports.signFormMultipleCallback = function (req, res) {
  var sHost = req.region.sHost;
  var sURL = sHost + '/';
  var formID = req.session.formID;
  var codeValue = req.query.code;
  var accessToken = req.session.access.accessToken;
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);
  var self = this;
  var newService = req.query.isNew ? JSON.parse(req.query.isNew) : false;

  if (!codeValue) {
    codeValue = req.query['amp;code'];
  }

  function loadFormAsync(callback) {
    loadForm(formID, sURL, function (error, response, body) {
      if (error) {
        callback(error, null);
      } else {
        callback(null, body);
      }
    });
  }

  function downloadSignedContent(formData, callback) {
    userService.downloadSignedContent(accessToken, codeValue, function (error, result) {
      callback(error, {signedContent: result, formData: formData});
    });
  }

  function cyrillicToLatin(word) {
    var a = {" ":" ","_":"_","а":"a","б":"b","в":"v","г":"g","д":"d","е":"e","ё":"e","ж":"zh","з":"z","и":"i","й":"y","к":"k","л":"l","м":"m","н":"n","о":"o","п":"p","р":"r","с":"s","т":"t","у":"u","ф":"f","х":"h","ц":"ts","ч":"ch","ш":"sh","щ":"sch","ъ":"","ы":"i","ь":"","э":"e","ю":"ju","я":"ja","А":"A","Б":"B","В":"V","Г":"G","Д":"D","Е":"E","Ё":"E","Ж":"Zh","З":"Z","И":"I","Й":"Y","К":"K","Л":"L","М":"M","Н":"N","О":"O","П":"P","Р":"R","С":"S","Т":"T","У":"U","Ф":"F","Х":"H","Ц":"Ts","Ч":"Ch","Ш":"Sh","Щ":"Sch","Ъ":"","Ы":"I","Ь":"","Э":"E","Ю":"Ju","Я":"Ja","a":"a","b":"b","c":"c","d":"d","e":"e","f":"f","g":"g","h":"h","i":"i","j":"j","k":"k","l":"l","m":"m","n":"n","o":"o","p":"p","q":"q","r":"r","s":"s","t":"t","u":"u","v":"v","w":"w","x":"x","y":"y","z":"z","A":"A","B":"B","C":"C","D":"D","E":"E","F":"F","G":"G","H":"H","I":"I","J":"J","K":"K","L":"L","M":"M","N":"N","O":"O","P":"P","Q":"Q","R":"R","S":"S","T":"T","U":"U","V":"V","W":"W","X":"X","Y":"Y","Z":"Z"};
    return word.split('').map(function (char) {
      return a[char] || char;
    }).join("");
  }

  function cyrillicToLatin(word) {
    var a = {" ":" ","_":"_","а":"a","б":"b","в":"v","г":"g","д":"d","е":"e","ё":"e","ж":"zh","з":"z","и":"i","й":"y","к":"k","л":"l","м":"m","н":"n","о":"o","п":"p","р":"r","с":"s","т":"t","у":"u","ф":"f","х":"h","ц":"ts","ч":"ch","ш":"sh","щ":"sch","ъ":"","ы":"i","ь":"","э":"e","ю":"ju","я":"ja","А":"A","Б":"B","В":"V","Г":"G","Д":"D","Е":"E","Ё":"E","Ж":"Zh","З":"Z","И":"I","Й":"Y","К":"K","Л":"L","М":"M","Н":"N","О":"O","П":"P","Р":"R","С":"S","Т":"T","У":"U","Ф":"F","Х":"H","Ц":"Ts","Ч":"Ch","Ш":"Sh","Щ":"Sch","Ъ":"","Ы":"I","Ь":"","Э":"E","Ю":"Ju","Я":"Ja","a":"a","b":"b","c":"c","d":"d","e":"e","f":"f","g":"g","h":"h","i":"i","j":"j","k":"k","l":"l","m":"m","n":"n","o":"o","p":"p","q":"q","r":"r","s":"s","t":"t","u":"u","v":"v","w":"w","x":"x","y":"y","z":"z","A":"A","B":"B","C":"C","D":"D","E":"E","F":"F","G":"G","H":"H","I":"I","J":"J","K":"K","L":"L","M":"M","N":"N","O":"O","P":"P","Q":"Q","R":"R","S":"S","T":"T","U":"U","V":"V","W":"W","X":"X","Y":"Y","Z":"Z"};
    return word.split('').map(function (char) {
      return a[char] || char;
    }).join("");
  }

  function processZipWithSignedContent(result, callback) {
    //TODO new async of decompressing files from archive, saving to redis, ids from redis to form data,
    //TODO form data to redis, new formdata id to redirect
    var zip = new admZip(result.signedContent.buffer);
    var zipEntries = zip.getEntries();

    var uploadedFiles = {};

    function uploadZipEntry(zipEntry, buffer, entryProcessCallback) {
      uploadFileService.upload([{
        name: 'file',
        options: {
          filename: zipEntry.name
        },
        buffer: buffer
      }], function (error, response, body) {
        if (!body) {
          entryProcessCallback(errors.createExternalServiceError('Can\'t save signed content from archive. Unknown error', error));
        } else if (body.code && body.message) {
          entryProcessCallback(errors.createExternalServiceError('Can\'t save signed content from archive. ' + body.message, body));
        } else if (body.sKey) {
          uploadedFiles[zipEntry.name.split('.')[0]] = JSON.stringify(body);
          entryProcessCallback();
        } else if (body.fileID) {
          uploadedFiles[zipEntry.name.split('.')[0]] = body.fileID;
          entryProcessCallback();
        }
      }, sHost, newService);
    }

    function processZipEntry(zipEntry, entryProcessCallback) {
      zip.readFileAsync(zipEntry, function (buffer) {
        uploadZipEntry(zipEntry, buffer, entryProcessCallback);
      });
    }

    function populateFormDataWithNewIDs(error) {
      if (error) {
        callback(error, null);
      } else {
        console.log(JSON.stringify(uploadedFiles));
        console.log(JSON.stringify(result.formData));

        var savedForm = result.formData;
        for (var formFieldKey in savedForm.formData.files) {
          if (savedForm.formData.files.hasOwnProperty(formFieldKey)) {
            if (uploadedFiles.hasOwnProperty(formFieldKey)) {
              var oldID = savedForm.formData.params[formFieldKey];
              var newID = uploadedFiles[formFieldKey];
              savedForm.formData.params[formFieldKey] = newID;
              console.log('[sign multiple callback] update fileids. was : ' + oldID + 'now : ' + newID);
            }
          }
        }

        for (var property in savedForm.formData.params) {
          if(savedForm.formData.params.hasOwnProperty(property) && savedForm.formData.params[property] !== null && typeof savedForm.formData.params[property] === 'object') {
            if('type' in savedForm.formData.params[property] && savedForm.formData.params[property].type === 'table' && savedForm.formData.params[property].aRow) {
              savedForm.formData.params[property].aRow.forEach(function (row) {
                row.aField.forEach(function (field) {
                  if(field.fileName)
                    var sLatin = cyrillicToLatin(field.fileName);
                  if(field.type === 'file' && sLatin && sLatin.split('.')[0] in uploadedFiles) {
                    field.value = uploadedFiles[sLatin.split('.')[0]];
                  }
                })
              })
            }
          }
        }

        for (var uploadedFileKey in uploadedFiles) {
          if (uploadedFiles.hasOwnProperty(uploadedFileKey) && uploadedFileKey.indexOf('signedForm') > -1) {
            var newID = uploadedFiles[uploadedFileKey];
            savedForm.formData.params['form_signed_all'] = newID;
            console.log('[sign multiple callback] update form_signed_all. was : ' + undefined + 'now : ' + newID);
          }
        }

        formService.saveForm(sHost, result.formData, function (error, response, body) {
          if (!body) {
            callback(errors.createExternalServiceError('Can\'t rewrite form. Unknown error', error), null);
          } else if (body.code && body.message) {
            callback(errors.createExternalServiceError('Can\'t rewrite form. ' + body.message, body), null);
            // } else if (body.fileID) {
            //   var oldFormID = formID;
            //   req.session.formID = body.fileID;
            //   formID = body.fileID;
          }else if (body.sKey) {
            var oldFormID = formID;
            req.session.formID = body.sKey;
            formID = body.sKey;
            console.log('[sign multiple callback] update form id. was : ' + oldFormID + 'now : ' + formID);
            callback(null, result);
          } else {
            callback(errors.createExternalServiceError('Can\'t rewrite form. Unknown response', {}), null);
          }
        });
      }
    }

    async.forEach(zipEntries, processZipEntry, populateFormDataWithNewIDs);
  }

  function processSingleFileWithSignedContent(result, callback) {
    uploadFileService.upload([{
      name: 'file',
      options: {
        filename: result.signedContent.fileName
      },
      buffer: result.signedContent.buffer
    }], function (error, response, body) {
      if (!body) {
        callback(errors.createExternalServiceError('Can\'t save signed content. Unknown error', error), null);
      } else if (body.code && body.message) {
        callback(errors.createExternalServiceError('Can\'t save content. ' + body.message, body), null);
      } else if (body.sKey) {
        result.signedFileID = body.sKey;
        callback(null, result);
      } else if (body.fileID) {
        result.signedFileID = body.fileID;
        callback(null, result);
      }
    }, sHost, newService);
  }

  function processSignedContent(result, callback) {
    if (result.signedContent.fileName.indexOf('.zip') > -1) {
      processZipWithSignedContent(result, callback);
    } else {
      processSingleFileWithSignedContent(result, callback);
    }
  }

  function processResult(err, result) {
    if (err) {
      res.redirect(result.formData.restoreFormUrl
        + '?formID=' + formID
        + '&error=' + JSON.stringify(err));
    } else {
      res.redirect(result.formData.restoreFormUrl
        + '?formID=' + formID
        + '&signedFileID=' + result.signedFileID);
    }
  }

  async.waterfall([
    loadFormAsync,
    downloadSignedContent,
    processSignedContent
  ], processResult);
};

module.exports.signForm = function (req, res) {
  var formID = req.session.formID;
  var oServiceDataNID = req.query.oServiceDataNID;
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);
  var nID_Server = req.query.nID_Server;
  var bConvertToPDF = req.query.bConvertToPDF ? req.query.bConvertToPDF : false;
  var newService = '';

  if (!userService.signHtmlForm) {
    res.status(400).send(errors.createError(errors.codes.LOGIC_SERVICE_ERROR,
      'type of authorization doesn\'t support html forms signing'));
    return;
  }

  activiti.getServerRegionHost(nID_Server, function (sHost) {
    var sURL = sHost + '/';
    console.log("sURL=" + sURL);

    //  var sURL = req.query.sURL;
    var sName = req.query.sName;


    if (!formID) {
      res.status(400).send({error: 'formID should be specified'});
    }

    if (!oServiceDataNID && !sURL) {
      res.status(400).send({error: 'Either sURL or oServiceDataNID should be specified'});
      return;
    }

    if(req.query.isNew) {
      var isNewService = JSON.parse(req.query.isNew);
      newService = '&isNew=' + isNewService;
    }

    var callbackURL = url.resolve(originalURL(req, {}), '/api/process-form/sign/callback?nID_Server=' + nID_Server + newService);
    if (oServiceDataNID) {
      req.session.oServiceDataNID = oServiceDataNID;
      //TODO use oServiceDataNID in callback
      //TODO fill sURL from oServiceData to use it below
    } else if (sURL) {
      req.session.sURL = sURL;
    }

    var createHtml = function (data, callback) {
      var formData = data.formData;
      var printFormData = data.formDataPrintPdf;

      var templateData = {
        formProperties: data.activitiForm.formProperties,
        processName: sName, //data.processName,
        businessKey: data.businessKey,
        creationDate: '' + new Date()
      };

      var patternFileName = null;

      templateData.formProperties.forEach(function (item) {
        var value = formData.params[item.id];
        if (value) {
          item.value = value;
        }
      });

      for (var key in formData.params) {
        if (formData.params.hasOwnProperty(key) && key.indexOf('PrintFormAutoSign_') === 0)
          patternFileName = formData.params[key];
      }

      if (patternFileName) {
        var reqParams = activiti.buildRequest(req, 'service/object/file/getPatternFile', {sPathFile: patternFileName.replace(/^pattern\//, '')}, sURL);
        request(reqParams, function (error, response, body) {
          for (var key in printFormData.params) {
            if (printFormData.params.hasOwnProperty(key)) {
              var keyValue = '[' + key + ']';
              body = body.split(keyValue).join(printFormData.params[key]);
            }
          }
          var dateCreate = new Date();
          var formatedDateCreate = dateCreate.getFullYear() + '-' + ('0' + (dateCreate.getMonth() + 1)).slice(-2) + '-' + ('0' + dateCreate.getDate()).slice(-2);
          body = body.split('[sDateCreateProcess]').join(formatedDateCreate);
          callback(body);
        });
      } else {
        callback(formTemplate.createHtml(templateData));
      }
    };

    async.waterfall([
      function (callback) {
        loadForm(formID, sURL, function (error, response, body) {
          if (error) {
            callback(error, null);
          } else {
            callback(null, body);
          }
        });
      },
      function (formData, callback) {
        var accessToken = req.session.access.accessToken;
        createHtml(formData, function (formToUpload) {
          if (bConvertToPDF === 'true' || bConvertToPDF == true) {
            userService.signPdfForm(accessToken, callbackURL, formToUpload, function (error, result) {
              if (error) {
                callback(error, null);
              } else {
                callback(null, result)
              }
            });
          } else {
            userService.signHtmlForm(accessToken, callbackURL, formToUpload, function (error, result) {
              if (error) {
                callback(error, null);
              } else {
                callback(null, result)
              }
            });
          }
        });
      }
    ], function (error, result) {
      if (error) {
        res.status(500).send(error);
      } else {
        res.redirect(result.desc);
      }
    });
  });
};

module.exports.signFormCallback = function (req, res) {
  var sHost = req.region.sHost;
  var sURL = sHost + '/';
  var formID = req.session.formID;
  var oServiceDataNID = req.session.oServiceDataNID;
  var codeValue = req.query.code;
  var newService = req.query.isNew ? JSON.parse(req.query.isNew) : false;
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);


  if (!codeValue) {
    codeValue = req.query['amp;code'];
  }

  if (oServiceDataNID) {
    //TODO fill sURL from oServiceData to use it below
    sURL = '';
  }

  if(!userService.prepareSignedContentRequest){
    res.status(400).send(errors.createError(errors.codes.LOGIC_SERVICE_ERROR,
      'type of authorization doesn\'t support html forms signing'));
    return;
  }

  var accessToken = req.session.access.accessToken;
  function downloadSignedContent(formData, callback) {
    logger.info('[signFormCallback] downloading signed content....', {accessToken: accessToken, codeValue: codeValue});
    userService.downloadSignedContent(accessToken, codeValue, function (error, result) {
      logger.info('[signFormCallback] ....downloaded signed content', { error: error, contentType: result.contentType, fileName: result.fileName });
      callback(error, {signedContent : result, formData: formData});
    });
  }

  function uploadSignedForm(downloadResult, callback) {
    logger.info('[signFormCallback] uploading signed content to redis....');
    uploadFileService.upload([{
      name: 'file',
      options: {
        filename: 'signedForm.pdf'
      },
      buffer: downloadResult.signedContent.buffer
    }], function (error, response, body) {
      logger.info('[signFormCallback] ....uploaded signed content to redis', {result: body});
      if (!body) {
        callback(errors.createExternalServiceError('Can\'t save signed content to storage. Unknown error', error), null);
      } else if (body.code && body.message) {
        callback(errors.createExternalServiceError('Can\'t save signed content to storage. ' + body.message, body), null);
      } else if (body.sKey) {
        downloadResult.signedFormID = JSON.stringify(body);
        callback(null, downloadResult);
      } else if (body.fileID) {
        downloadResult.signedFormID = body.fileID;
        callback(null, downloadResult);
      }

    }, sHost, newService);
  }

  async.waterfall([
    function (callback) {
      loadForm(formID, sURL, function (error, response, body) {
        logger.info('[signFormCallback] ..... form is loaded');
        if (error) {
          callback(error, null);
        } else {
          callback(null, body);
        }
      });
    },
    downloadSignedContent,
    uploadSignedForm
  ], function (err, result) {
    if (err) {
      logger.error('error. go back to initial page', {error: err});
      res.redirect(result.formData.restoreFormUrl
        + '?formID=' + formID
        + '&error=' + JSON.stringify(err));
    } else {
      logger.info('cool go back to initial page');
      res.redirect(result.formData.restoreFormUrl
        + '?formID=' + formID
        + '&signedFileID=' + result.signedFormID);
    }
  });

};

module.exports.saveForm = function (req, res) {
  var sHost = req.region.sHost;
  var data = req.body;

  formService.saveForm(sHost, data, function (error, response, body) {
    if (!body) {
      res.status(500).send(errors.createExternalServiceError('Can\'t save form. Unknown error', error));
    } else if (body.code && body.message) {
      res.status(500).send(errors.createExternalServiceError('Can\'t save form. ' + body.message, body));
    // } else if (body.fileID) {
    //   req.session.formID = body.fileID;
    //   res.send({formID: body.fileID});
    // }
    } else if (body.sKey) {
      req.session.formID = body.sKey;
      res.send({formID: body.sKey});
    }
  });
};

module.exports.loadForm = function (req, res) {
  var formID = req.query.formID;
  var sHost = req.region.sHost;
  var sURL = sHost + '/';

  var callback = function (error, response, body) {
    if (error) {
      res.status(400).send(error);
    } else {
      res.send(body);
    }
  };

  loadForm(formID, sURL, callback);
};

function loadForm(formID, sURL, callback) {
  // var downloadURL = sURL + 'service/object/file/download_file_from_redis_bytes';
  var downloadURL = sURL + 'service/object/file/getProcessAttach';
  request.get({
    url: downloadURL,
    auth: getAuth(),
    // qs: {
    //   key: formID
    // },
    qs: {
      sKey: formID,
      sID_StorageType : 'Redis'
    },
    json: true
  }, callback);
}

function pipeFormDataToRequest(form, requestOptionsForUploadContent, callback) {
  var decoder = new StringDecoder('utf8');
  var result = {};
  form.pipe(
    request.post(requestOptionsForUploadContent)
      .on('response', function (response) {
        logger.info("[requestForUploadContent] response", {
          loading_from: requestOptionsForUploadContent,
          res_status: response.statusCode,
          res_headers: response.headers
        });
      })
      .on('error', function (e) {
        callback(e, null)
      })
  ).on('response', function (response) {
    logger.info("[pipeFormDataToRequest] response", {
      res_status: response.statusCode,
      res_headers: response.headers
    });
    result.statusCode = response.statusCode;
  }).on('data', function (chunk) {
    if (result.data) {
      result.data += decoder.write(chunk);
    } else {
      result.data = decoder.write(chunk);
    }
  }).on('error', function (e) {
    callback(e, null)
  }).on('end', function () {
    callback(null, result);
  });
}

var originalURL = function (req, options) {
  options = options || {};
  var app = req.app;
  if (app && app.get && app.get('trust proxy')) {
    options.proxy = true;
  }
  var trustProxy = options.proxy;

  var proto = (req.headers['x-forwarded-proto'] || '').toLowerCase()
    , tls = req.connection.encrypted || (trustProxy && 'https' == proto.split(/\s*,\s*/)[0])
    , host = (trustProxy && req.headers['x-forwarded-host']) || req.headers.host
    , protocol = tls ? 'https' : 'http'
    , path = req.url || '';
  var originalURL = protocol + '://' + host + path;
  console.log('[sign] originalURL = ' + originalURL);
  return originalURL;
};

function getOptions() {
  var config = require('../../config/environment');

  var oConfigServerExternal = config.activiti;

  return {
    protocol: oConfigServerExternal.protocol,
    hostname: oConfigServerExternal.hostname,
    port: oConfigServerExternal.port,
    path: oConfigServerExternal.path,
    username: oConfigServerExternal.username,
    password: oConfigServerExternal.password
  };
}

function getAuth() {
  var options = getOptions();
  return {
    'username': options.username,
    'password': options.password
  };
}
