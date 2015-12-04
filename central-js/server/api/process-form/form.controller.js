var url = require('url');
var request = require('request');
var FormData = require('form-data');
var config = require('../../config/environment');
var accountService = require('../../auth/bankid/bankid.service.js');
var _ = require('lodash');
var StringDecoder = require('string_decoder').StringDecoder;
var async = require('async');
var formTemplate = require('./form.template');
var activiti = require('../../components/activiti');
var errors = require('../../components/errors');

module.exports.index = function (req, res) {
  var activiti = config.activiti;

  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };

  return request.get({
    url: req.query.url,
    auth: {
      username: activiti.username,
      password: activiti.password
    }
  }, callback);
};

module.exports.submit = function (req, res) {
  var activiti = config.activiti;

  var options = {
    protocol: activiti.protocol,
    hostname: activiti.hostname,
    port: activiti.port,
    path: activiti.path,
    username: activiti.username,
    password: activiti.password,
    formData: req.body
  };

  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };

  var nID_Subject = req.session.subject.nID;
  var properties = [];
  for (var id in options.formData.params) {
    var value = options.formData.params[id];
    if (id === 'nID_Subject') {
      value = nID_Subject;
    }
    if (id === 'sID_UA' && options.formData.sID_UA_Common !== null) {
      //if (id === 'sID_UA_Common') {
      value = options.formData.sID_UA_Common;
    } else if (id === 'sID_UA') {
      value = options.formData.sID_UA;
    }

    properties.push({
      id: id,
      value: value
    });
  }

  return request.post({
    url: options.formData.url || null,
    auth: {
      username: options.username,
      password: options.password
    },
    body: {
      processDefinitionId: options.formData.processDefinitionId,
      businessKey: "key",
      properties: properties,
      nID_Subject: nID_Subject
    },
    qs: {
      nID_Service: options.formData.nID_Service,
      nID_Region: options.formData.nID_Region,
      sID_UA: options.formData.sID_UA
    },
    json: true
  }, callback);
};

module.exports.scanUpload = function (req, res) {
  var accessToken = req.session.access.accessToken;
  var data = req.body;
  var uploadURL = data.url;
  var documentScans = data.scanFields;

  var uploadResults = [];
  var uploadScan = function (documentScan, callback) {
    var scanContentRequest = accountService.prepareScanContentRequest(documentScan.scan.link, accessToken);

    var form = new FormData();
    form.append('file', scanContentRequest, {
      filename: documentScan.scan.type + '.' + documentScan.scan.extension
    });

    var requestOptionsForUploadContent = {
      url: uploadURL,
      auth: getAuth(),
      headers: form.getHeaders()
    };

    pipeFormDataToRequest(form, requestOptionsForUploadContent, function (result) {
      uploadResults.push({
        fileID: result.data,
        scanField: documentScan
      });
      callback();
    });
  };

  async.forEach(documentScans, function (documentScan, callback) {
    uploadScan(documentScan, callback);
  }, function (error) {
    res.send(uploadResults);
    res.end();
  });

};

module.exports.signCheck = function (req, res) {
  var fileID = req.query.fileID;
  var sURL = req.query.sURL;

  if (!fileID) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, 'fileID should be specified'));
    return;
  }

  if (!sURL) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, 'sURL should be specified'));
    return;
  }

  var reqParams = activiti.buildRequest(req, 'service/rest/file/check_file_from_redis_sign', {
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

module.exports.signForm = function (req, res) {
  var formID = req.session.formID;
  var oServiceDataNID = req.query.oServiceDataNID;
  var sURL = req.query.sURL;
  var sName = req.query.sName;


  if (!formID) {
    res.status(400).send({error: 'formID should be specified'});
  }

  if (!oServiceDataNID && !sURL) {
    res.status(400).send({error: 'Either sURL or oServiceDataNID should be specified'});
    return;
  }

  var callbackURL = url.resolve(originalURL(req, {}), '/api/process-form/sign/callback');
  if (oServiceDataNID) {
    req.session.oServiceDataNID = oServiceDataNID;
    //TODO use oServiceDataNID in callback
    //TODO fill sURL from oServiceData to use it below
  } else if (sURL) {
    req.session.sURL = sURL;
  }

  var createHtml = function (data, callback) {
    var formData = data.formData;

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
      var reqParams = activiti.buildRequest(req, '/wf/service/rest/getPatternFile', {sPathFile: patternFileName.replace(/^pattern\//, '')}, config.server.sServerRegion);
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
        accountService.signHtmlForm(accessToken, callbackURL, formToUpload, function (error, result) {
          if (error) {
            callback(error, null);
          } else {
            callback(null, result)
          }
        });
      });
    }
  ], function (error, result) {
    if (error) {
      res.status(500).send(error);
    } else {
      res.redirect(result.desc);
    }
  });
};

module.exports.signFormCallback = function (req, res) {
  var sURL = req.session.sURL;
  var formID = req.session.formID;
  var oServiceDataNID = req.session.oServiceDataNID;
  var codeValue = req.query.code;

  if (oServiceDataNID) {
    //TODO fill sURL from oServiceData to use it below
    sURL = '';
  }

  var signedFormForUpload = accountService
    .prepareSignedContentRequest(req.session.access.accessToken, codeValue);

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
      var signedFormUpload = sURL + 'service/rest/file/upload_file_to_redis';
      var form = new FormData();
      form.append('file', signedFormForUpload, {
        filename: 'signedForm.pdf'
      });

      var requestOptionsForUploadContent = {
        url: signedFormUpload,
        auth: getAuth(),
        headers: form.getHeaders()
      };

      pipeFormDataToRequest(form, requestOptionsForUploadContent, function (result) {
        callback(null, {formData: formData, signedFormID: result.data});
      });
    }
  ], function (err, result) {
    if (err) {
      res.redirect(result.formData.restoreFormUrl
        + '?formID=' + formID
        + '&error=' + JSON.stringify(err));
    } else {
      res.redirect(result.formData.restoreFormUrl
        + '?formID=' + formID
        + '&signedFileID=' + result.signedFormID);
    }
  });

};

module.exports.saveForm = function (req, res) {
  var data = req.body;
  var oServiceDataNID = req.query.oServiceDataNID;
  var sURL = req.query.sURL;

  if (oServiceDataNID) {
    //TODO fill sURL from oServiceData to use it below
    sURL = '';
  }

  var uploadURL = sURL + 'service/rest/file/upload_file_to_redis';

  var form = new FormData();
  form.append('file', JSON.stringify(data), {
    filename: 'formData.json'
  });

  var requestOptionsForUploadContent = {
    url: uploadURL,
    auth: getAuth(),
    headers: form.getHeaders()
  };

  pipeFormDataToRequest(form, requestOptionsForUploadContent, function (result) {
    req.session.formID = result.data;
    if (oServiceDataNID) {
      req.session.oServiceDataNID = oServiceDataNID;
    } else {
      req.session.sURL = sURL;
    }
    res.send({formID: result.data});
  });
};

module.exports.loadForm = function (req, res) {
  var formID = req.query.formID;
  var sURL = req.query.sURL;

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
  var downloadURL = sURL + 'service/rest/file/download_file_from_redis_bytes';
  request.get({
    url: downloadURL,
    auth: getAuth(),
    qs: {
      key: formID
    },
    json: true
  }, callback);
}

function pipeFormDataToRequest(form, requestOptionsForUploadContent, callback) {
  var decoder = new StringDecoder('utf8');
  var result = {};
  form.pipe(request.post(requestOptionsForUploadContent))
    .on('response', function (response) {
      result.statusCode = response.statusCode;
    }).on('data', function (chunk) {
    if (result.data) {
      result.data += decoder.write(chunk);
    } else {
      result.data = decoder.write(chunk);
    }
  }).on('end', function () {
    callback(result);
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
  return protocol + '://' + host + path;
};

function getOptions() {
  var config = require('../../config/environment');
  var activiti = config.activiti;

  return {
    protocol: activiti.protocol,
    hostname: activiti.hostname,
    port: activiti.port,
    path: activiti.path,
    username: activiti.username,
    password: activiti.password
  };
}

function getAuth() {
  var options = getOptions();
  return {
    'username': options.username,
    'password': options.password
  };
}
