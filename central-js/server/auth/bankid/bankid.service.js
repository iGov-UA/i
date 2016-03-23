var request = require('request');
var FormData = require('form-data');
var async = require('async');
var _ = require('lodash');
var config = require('../../config/environment');
var syncSubject = require('../../api/subject/subject.service.js');
var Admin = require('../../components/admin/index');
var url = require('url');
var bankidUtil = require('./bankid.util.js');

var createError = function (error, error_description, response) {
  return {
    code: response ? response.statusCode : 500,
    err: {
      error: error,
      error_description: error_description
    }
  };
};

var decryptCallback = function (callback) {
  return function (error, response, body) {
    if (config.bankid.enableCipher && body && body.customer && body.customer.signature) {
      bankidUtil.decryptData(body.customer);
    }
    callback(error, response, body);
  }
};

module.exports.index = function (accessToken, callback) {
  var url = bankidUtil.getInfoURL(config);

  var adminCheckCallback = function (error, response, body) {
    if (body.customer && Admin.isAdminInn(body.customer.inn)) {
      body.admin = {
        inn: body.customer.inn,
        token: Admin.generateAdminToken()
      };
    }
    callback(error, response, body);
  };

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
  }, decryptCallback(adminCheckCallback));
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
  }, decryptCallback(callback));
};

module.exports.prepareScanContentRequest = function (documentScanLink, accessToken) {
  var o = {
    'url': documentScanLink,
    'headers': {
      'Authorization': bankidUtil.getAuth(accessToken)
    }
  };
  return request.get(o);
};

module.exports.syncWithSubject = function (accessToken, done) {
  async.waterfall([
    function (callback) {
      module.exports.index(accessToken, function (error, response, body) {
        if (error || body.error || !body.customer) {
          callback(createError(error || body.error || body, body.error_description, response), null);
        } else {
          callback(null, {
            customer: body.customer,
            admin: body.admin
          });
        }
      });
    },

    function (result, callback) {
      syncSubject.sync(result.customer.inn, function (error, response, body) {
        if (error) {
          callback(createError(error, response), null);
        } else {
          result.subject = body;
          callback(null, result);
        }
      });
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

/**
 * После отработки п.3 (подписание), BankID делает редирект на https://{PI:port}/URL_callback?code=code_value с передачей
 * параметра авторизационного ключа code, тем самым заканчивая фазу п.4.
 * https://{PI:port}/ResourceService/checked/claim/code_value/clientPdfClaim
 * @param accessToken
 * @param codeValue
 */
module.exports.prepareSignedContentRequest = function (accessToken, codeValue) {
  return module.exports.prepareScanContentRequest(bankidUtil.getClientPdfClaim(codeValue), accessToken);
};

