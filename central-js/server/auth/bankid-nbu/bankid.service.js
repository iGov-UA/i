var request = require('request')
  , FormData = require('form-data')
  , async = require('async')
  , _ = require('lodash')
  , config = require('../../config/environment')
  , syncSubject = require('../../api/subject/subject.service')
  , Admin = require('../../components/admin/index')
  , url = require('url')
  , StringDecoder = require('string_decoder').StringDecoder
  , bankidNBUUtil = require('./bankid.util')
  , errors = require('../../components/errors')
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

module.exports.decryptCallback = function (callback) {
  bankidNBUUtil.decryptCallback(callback);
};

module.exports.convertToCanonical = function (customer) {
  // сохранение признака для отображения надписи о необходимости проверки регистрационных данных, переданых от BankID
  customer.isAuthTypeFromBankID = true;
  return customer;
};

module.exports.getUserKeyFromSession = function (session) {
  return session.access.accessToken;
};

function responseContractValidation(callback, nextCallback) {
  return function (error, response, body) {
    if (response.statusCode === 200 && (body.customer || body.customerCrypto)) {
      nextCallback(error, response, changeToCustomer(body));
    } else if (response.statusCode === 200 && body.error) {
      // HTTP/1.1 406 Not Acceptable , якщо у запиті не задано ідентифікатор ПАП ,
      // HTTP/1.1 501 Not Implemented , якщо у банку відсутній сертифікат ПАП.
      callback(error, response, body);
    } else {
      callback(error, response, body);
    }
  }
}

function changeToCustomer(body) {
  if (body.customerCrypto) {
    body.customer = body.customerCrypto;
    delete body.customerCrypto;
  }
  return body;
}

module.exports.index = function (accessToken, callback, disableDecryption) {
  var url = bankidNBUUtil.getInfoURL(config);

  function adminCheckCallback(error, response, body) {
    console.log("--------------- enter admin callback !!!!");
    var innToCheck;

    if (disableDecryption) {
      console.log("---------------  innToCheck before decryption !!!!" + body.customer.inn);
      innToCheck = bankidNBUUtil.decryptField(body.customer.inn);
      console.log("---------------  innToCheck after decryption !!!!" + innToCheck);
    } else {
      innToCheck = body.customer.inn;
      console.log("--------------- nodecrption of inn !!!!");
    }

    console.log("---------------  innToCheck in result !!!!" + innToCheck);

    console.log("---------------  body.customer in result !!!!" + body.customer);
    console.log("---------------  Admin.isAdminInn(innToCheck) in result!!!! " + Admin.isAdminInn(innToCheck));

    if (body.customer && Admin.isAdminInn(innToCheck)) {
      console.log("---------------  user with inn " + innToCheck + " is admin");
      body.admin = {
        inn: innToCheck,
        token: Admin.generateAdminToken()
      };
    }
    callback(error, response, body);
  }

  var resultCallback;

  if (disableDecryption) {
    resultCallback = adminCheckCallback;
  } else {
    resultCallback = bankidNBUUtil.decryptCallback(adminCheckCallback);
  }

  return request.post({
    'url': url,
    'headers': {
      'Content-Type': 'application/json',
      'Authorization': bankidNBUUtil.getAuth(accessToken),
      'Accept': 'application/json'
    },
    json: true,
    body: {
      "type": "physical",
      "fields": ["firstName", "middleName", "lastName", "phone", "inn", "birthDay", "email"],

      "addresses": [
        {
          "type": "factual",
          "fields": ["country", "state", "area", "city", "street", "houseNo", "flatNo"]
        },
        {
          "type": "birth",
          "fields": ["country", "state", "area", "city", "street", "houseNo", "flatNo"]
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
  }, responseContractValidation(callback, resultCallback));
};

module.exports.scansRequest = function (accessToken, callback) {
  var url = bankidNBUUtil.getInfoURL();
  return request.post({
    'url': url,
    'headers': {
      'Content-Type': 'application/json',
      'Authorization': bankidNBUUtil.getAuth(accessToken),
      'Accept': 'application/json'
    },
    json: true,
    body: {
      "type": "physical",
      "fields": ["firstName", "middleName", "lastName", "phone", "inn", "birthDay"],
      "scans": [{
        "type": "passport",
        "fields": ["link", "dateCreate", "extension"]
      }, {
        "type": "zpassport",
        "fields": ["link", "dateCreate", "extension"]
      }]
    }
  }, bankidNBUUtil.decryptCallback(callback));
};

module.exports.getScanContentRequestOptions = function (documentScanLink, accessToken) {
  return {
    url: documentScanLink,
    headers: {
      Authorization: bankidNBUUtil.getAuth(accessToken)
    },
    encoding: null
  };
};

module.exports.getScanContentRequest = function (documentScanLink, accessToken) {
  return request.get(this.getScanContentRequestOptions(documentScanLink, accessToken));
};

/**
 * function downloads buffer with scan bytes
 *
 * @param documentScanType type of scan
 * @param documentScanLink link to scan from where we should download it
 * @param accessToken access token from bankid authorization
 * @param callback function(error, buffer)
 */
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
        if (error || body.error || (!body.customer && !body.customerCrypto)) {
          callback(createError(error || body.error || body, body.error_description, response), null);
        } else {
          var customerAndAdmin = {
            customer: body.customer,
            admin: body.admin
          };
          if (body.customer) {
            customerAndAdmin.customer = body.customer;
          } else if (body.customerCrypto) {
            customerAndAdmin.customerCrypto = body.customerCrypto;
          }
          callback(null, customerAndAdmin);
        }
      }, disableDecryption);
    },

    function (result, callback) {
      var inn = result.customer ? result.customer.inn : bankidNBUUtil.decryptFieldInn(result.customerCrypto);
      syncSubject.sync(inn, function (error, response, body) {
        if (error) {
          callback(createError(error, response), null);
        } else {
          result.subject = body;
          callback(null, result);
        }
      });
    },

    function (result, callback) {
      self.cacheCustomer(result, function (error, reponse, body) {
        if (error || body.code) {
          callback(createError(body, 'error while caching data. ' + body.message, response), null);
        } else {
          result.usercacheid = body;

          var inn = result.customer ? result.customer.inn : bankidNBUUtil.decryptFieldInn(result.customerCrypto);
          var firstName = result.customer ? result.customer.firstName : bankidNBUUtil.decryptFieldFirstName(result.customerCrypto);
          var middleName = result.customer ? result.customer.middleName : bankidNBUUtil.decryptFieldMiddleName(result.customerCrypto);
          var lastName = result.customer ? result.customer.lastName : bankidNBUUtil.decryptFieldLastName(result.customerCrypto);

          if (result.customer.inn) {
            result.customer.inn = bankidNBUUtil.decryptField(result.customer.inn);
          }
          if (result.customer.firstName) {
            result.customer.firstName = bankidNBUUtil.decryptField(result.customer.firstName);
          }
          if (result.customer.middleName) {
            result.customer.middleName = bankidNBUUtil.decryptField(result.customer.middleName);
          }
          if (result.customer.lastName) {
            result.customer.lastName = bankidNBUUtil.decryptField(result.customer.lastName);
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
  var uploadURL = bankidNBUUtil.getUploadFileForSignatureURL();

  var form = new FormData();
  form.append('file', formToUpload, {
    contentType: 'text/html'
  });

  var requestOptionsForUploadContent = {
    url: uploadURL,
    headers: _.merge({
      Authorization: bankidNBUUtil.getAuth(accessToken),
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
      var uploadURL = bankidNBUUtil.getUploadFileForSignatureURL();
      var requestOptionsForUploadContent = {
        url: uploadURL,
        headers: _.merge({
          Authorization: bankidNBUUtil.getAuth(accessToken),
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
  return module.exports.getScanContentRequest(bankidNBUUtil.getClientPdfClaim(codeValue), accessToken);
};

