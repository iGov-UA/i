/**
 * Created by Igor on 5/12/2016.
 */
var request = require('request')
  , FormData = require('form-data')
  , async = require('async')
  , _ = require('lodash')
  , config = require('../../config/environment')
  , syncSubject = require('../../api/subject/subject.service.js')
  , Admin = require('../../components/admin/index')
  , url = require('url')
  , bankidUtil = require('../bankid/bankid.util.js')
  , activiti = require('../../components/activiti');
var pdfConversion = require('phantom-html-to-pdf')();

var createError = function (error, error_description, response) {
  return {
    code: response ? response.statusCode : 500,
    err: {
      error: error,
      error_description: error_description
    }
  };
};

module.exports.index = function (accessToken, callback, disableDecryption) {
  var url = bankidUtil.getInfoURL(config);

  function adminCheckCallback(error, response, body) {
    console.log("--------------- enter admin callback !!!!");
    var innToCheck;

    if(disableDecryption){
      console.log("---------------  innToCheck before decryption !!!!" + body.customer.inn);
      innToCheck = bankidUtil.decryptField(body.customer.inn);
      console.log("---------------  innToCheck after decryption !!!!" + innToCheck);
    } else {
      innToCheck = body.customer.inn;
      console.log("--------------- nodecrption of inn !!!!");
    }

    console.log("---------------  innToCheck in result !!!!" + innToCheck);

    if (body.customer && Admin.isAdminInn(innToCheck)) {
      body.admin = {
        inn: innToCheck,
        token: Admin.generateAdminToken()
      };
    }
    callback(error, response, body);
  }

  var resultCallback;

  if(disableDecryption){
    resultCallback = adminCheckCallback;
  } else {
    resultCallback = bankidUtil.decryptCallback(adminCheckCallback);
  }

  return request.post({
    'url': url,
    'headers': {
      'Content-Type': 'application/json',
      'Authorization': bankidUtil.getAuth(accessToken),
      'Accept': 'application/json'
    },
    json: true,
    body: {
      "type": "physical",
      "fields": ["firstName", "middleName", "lastName", "phone", "inn", "clId", "clIdText", "birthDay", "email"],

      "addresses": [
        {
          "type": "factual",
          "fields": ["country", "state", "area", "city", "street", "houseNo", "flatNo", "dateModification"]
        },
        {
          "type": "birth",
          "fields": ["country", "state", "area", "city", "street", "houseNo", "flatNo", "dateModification"]
        }
      ],

      "documents": [{
        "type": "passport",
        "fields": ["series", "number", "issue", "dateIssue", "dateExpiration", "issueCountryIso2"]
      }],

      "scans": [{
        "type": "passport",
        "fields": ["link", "dateCreate", "extension"]
      }, {
        "type": "zpassport",
        "fields": ["link", "dateCreate", "extension"]
      }]
    }
  }, resultCallback);
};

module.exports.scansRequest = function (accessToken, callback) {
  var url = bankidUtil.getInfoURL();
  return request.post({
    'url': url,
    'headers': {
      'Content-Type': 'application/json',
      'Authorization': bankidUtil.getAuth(accessToken),
      'Accept': 'application/json'
    },
    json: true,
    body: {
      "type": "physical",
      "fields": ["firstName", "middleName", "lastName", "phone", "inn", "clId", "clIdText", "birthDay"],
      "scans": [{
        "type": "passport",
        "fields": ["link", "dateCreate", "extension"]
      }, {
        "type": "zpassport",
        "fields": ["link", "dateCreate", "extension"]
      }]
    }
  }, bankidUtil.decryptCallback(callback));
};

module.exports.getScanContentRequestOptions = function (documentScanLink, accessToken) {
  return {
    url: documentScanLink,
    headers: {
      Authorization: bankidUtil.getAuth(accessToken)
    },
    encoding: null
  };
};

module.exports.getScanContentRequest = function (documentScanLink, accessToken) {
  var o = {
    'url': documentScanLink,
    'headers': {
      'Authorization': bankidUtil.getAuth(accessToken)
    }
  };
  return request.get(o);
};

module.exports.cacheCustomer = function (customer, callback) {
  var url = '/object/file/upload_file_to_redis';
  activiti.upload(url, {}, 'customerData.json', JSON.stringify(customer), callback);
};

module.exports.syncWithSubject = function (accessToken, done) {
  var self = this;
  var disableDecryption = true;

  async.waterfall([
    function (callback) {
      self.index(accessToken, function (error, response, body) {
        if (error || body.error || !body.customer) {
          callback(createError(error || body.error || body, body.error_description, response), null);
        } else {
          callback(null, {
            customer: body.customer,
            admin: body.admin
          });
        }
      }, disableDecryption);
    },

    function (result, callback) {
      syncSubject.sync(bankidUtil.decryptField(result.customer.inn), function (error, response, body) {
        if (error) {
          callback(createError(error, response), null);
        } else {
          result.subject = body;
          callback(null, result);
        }
      });
    },

    function(result, callback){
      self.cacheCustomer(result, function(error, reponse, body){
        if (error || body.code) {
          callback(createError(body, 'error while caching data. ' + body.message, response), null);
        } else {
          result.usercacheid = body;

          if(result.customer.inn){
            result.customer.inn = bankidUtil.decryptField(result.customer.inn);
          }
          if(result.customer.firstName){
            result.customer.firstName = bankidUtil.decryptField(result.customer.firstName);
          }
          if(result.customer.middleName){
            result.customer.middleName = bankidUtil.decryptField(result.customer.middleName);
          }
          if(result.customer.lastName){
            result.customer.lastName = bankidUtil.decryptField(result.customer.lastName);
          }

          callback(null, result);
        }
      })
    }
  ], function (err, result) {
    done(err, result);
  });
};

/**
 *  Content-Type = "multipart/form-data"
 * Authorization = "Bearer access_token, Id client_id" - (последовательность не важна)
 * Accept = "application/json"
 * acceptKeyUrl = URL_callback - (урл перенаправления браузера и передача ключа для забора подписанного PDF)
 * fileType = "pdf" (так же принимает еще значения html, image, которые будут преобразованы в формат PDF )
 * В результате ответа будет получен JSON вида
 * {"state":"ok","code":"000000","desc":"https://{IP:port}/IdentDigitalSignature/signPdf?sidBi=sidBi_value"}
 *
 * @param accessToken
 * @param acceptKeyUrl
 * @param formToUpload
 * @param callback
 */
module.exports.signHtmlForm = function (accessToken, acceptKeyUrl, formToUpload, callback) {
  var uploadURL = bankidUtil.getUploadFileForSignatureURL();

  var form = new FormData();
  form.append('file', formToUpload, {
    contentType: 'text/html'
  });

  var requestOptionsForUploadContent = {
    url: uploadURL,
    headers: _.merge({
      Authorization: bankidUtil.getAuth(accessToken),
      acceptKeyUrl: acceptKeyUrl,
      fileType: 'html'
    }, form.getHeaders()),
    formData: {
      file: formToUpload
    },
    json: true
  };

  request.post(requestOptionsForUploadContent, function (error, response, body) {
    if (!body)
      callback('Unable to sign a file. bankid.privatbank.ua return an empty response', null);
    else if (error || (error = body.error)) {
      callback(error, null);
    } else {
      callback(null, body);
    }
  });
};

module.exports.signPdfForm = function (accessToken, acceptKeyUrl, formToUpload, callback) {
  async.waterfall([
    function (callback) {
      var options = {
        html: formToUpload,
        allowLocalFilesAccess: true,
        paperSize: {
          format: 'A4', orientation: 'portrait'
        },
        fitToPage: true,
        customHeaders: [],
        settings: {
          javascriptEnabled: true
        },
        format: {
          quality: 100
        }
      };
      pdfConversion(options, function (err, pdf) {
        callback(err, {content: pdf.stream, contentType: 'application/pdf'});
      });
    },
    function (data, callback) {
      var uploadURL = bankidUtil.getUploadFileForSignatureURL();
      var requestOptionsForUploadContent = {
        url: uploadURL,
        headers: _.merge({
          Authorization: bankidUtil.getAuth(accessToken),
          acceptKeyUrl: acceptKeyUrl,
          fileType: 'pdf'
        }, data.contentType),
        formData: {
          file: data.content
        },
        json: true
      };

      request.post(requestOptionsForUploadContent, function (error, response, body) {
        if (!body) {
          pdfConversion.kill();
          callback('Unable to sign a file. bankid.privatbank.ua return an empty response', null);
        } else if (error || (error = body.error)) {
          pdfConversion.kill();
          callback(error, null);
        } else {
          callback(null, body);
        }
      });
    }
  ], function (error, body) {
    if (error) {
      callback(error, null);
    } else {
      callback(null, body);
    }
  });
};

/**
 * После отработки п.3 (подписание), BankID делает редирект на https://{PI:port}/URL_callback?code=code_value с передачей
 * параметра авторизационного ключа code, тем самым заканчивая фазу п.4.
 * https://{PI:port}/ResourceService/checked/claim/code_value/clientPdfClaim
 * @param accessToken
 * @param codeValue
 */
module.exports.prepareSignedContentRequest = function (accessToken, codeValue) {
  return module.exports.getScanContentRequest(bankidUtil.getClientPdfClaim(codeValue), accessToken);
};


module.exports.scanContentRequest = function (documentScanType, documentScanLink, accessToken, callback) {
  var scanContentRequestOptions = this.getScanContentRequestOptions(documentScanLink, accessToken);
  request.get(scanContentRequestOptions, function (error, response, buffer) {
    if (!error && response.headers['content-type'].indexOf('application/octet-stream') > -1) {
      callback(null, buffer);
    } else if (!error &&
      (response.headers['content-type'].indexOf('application/json') > -1
      || response.headers['content-type'].indexOf('application/xml') > -1)) {
      var decoder = new StringDecoder('utf8');
      callback(errors.createExternalServiceError('Can\'t get scan upload of ' + documentScanType, decoder.write(buffer)));
    } else if (error) {
      callback(errors.createExternalServiceError('Can\'t get scan upload of ' + documentScanType, error));
    }
  });
};

