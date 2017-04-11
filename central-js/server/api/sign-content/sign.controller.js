var url = require('url')
  , async = require('async')
  , request = require('request')
  , authProviderRegistry = require('../../auth/auth.provider.registry')
  , activiti = require('../../components/activiti')
  , uploadFileService = require('../uploadfile/uploadfile.service')
  , errors = require('../../components/errors');

/**
 * Load previously saved content from redis, sign it, and call(callback) service for saving signed content back to redis
 * https://github.com/e-government-ua/i/issues/1374
 * @param req
 * @param res
 */
module.exports.signContent = function (req, res) {
  var formID = req.session.formID
    , sHost = req.region.sHost
    , sURL = sHost + '/'
    , type = req.session.type
    , userService = authProviderRegistry.getUserService(type)
    , storageType = 'Redis';


  /**
   * Load previously saved form from redis
   * @param callbackAsync
   */
  function getContentAsync(callbackAsync) {
    loadContent(formID, sURL, function (error, response, body) {
      if (error) {
        callbackAsync(error, null);
      } else {
        callbackAsync(null, {loadedContent: body});
      }
    });
  }

  var objectsToSign = [];

  function getContentBuffersAsync(result, callbackAsync) {
    var params = {};
        params.ID = formID;
        params.storageType = storageType;
    uploadFileService.downloadBuffer(params, function (error, response, buffer) {
      objectsToSign.push({
        name: 'file',
        options: {
          filename: buffer.formData.contentToSign.contentName
        },
        buffer: buffer.formData.contentToSign.contentValue
      });

      callbackAsync(null, result);
    }, sHost);
  }

  /**
   * Sign content and create module.exports.callback link in order to render form for further submit
   * @param result
   * @param callbackAsync
   */
  function signContentAsync(result, callbackAsync) {

    var mail = result.loadedContent.formData.params.email;
    var formInn = result.loadedContent.formData.params.bankIdinn;

    var fiscalData = {
      "customerType":"physical",
      "fiscalClaimAction":"encrypt",
      "customerInn": formInn ? formInn : req.session.subject.sID,
      "email": mail
    };
    var fiscalHeader = JSON.stringify(fiscalData);
    userService.signFiles(
      req.session.access.accessToken,
      url.resolve(
        originalURL(req, {}),
        '/api/sign-content/sign/callback?nID_Server=' + req.query.nID_Server
      ),
      objectsToSign,
      { fiscalData: fiscalHeader },
      function (error, signResult) {
        if (error) {
          callbackAsync(error, result);
        } else {
          result.signResult = signResult;
          callbackAsync(null, result);
        }
      });

    function originalURL(req, options) {
      options = options || {};
      var app = req.app;
      if (app && app.get && app.get('trust proxy')) {
        options.proxy = true;
      }

      var proto = (req.headers['x-forwarded-proto'] || '').toLowerCase(),
        trustProxy = options.proxy,
        tls = req.connection.encrypted || (trustProxy && 'https' == proto.split(/\s*,\s*/)[0]),
        host = (trustProxy && req.headers['x-forwarded-host']) || req.headers.host,
        protocol = tls ? 'https' : 'http',
        path = req.url || '';

      return protocol + '://' + host + path;
    }
  }

  async.waterfall([
    getContentAsync,
    getContentBuffersAsync,
    signContentAsync
  ], function (error, result) {
    if (error) {
      res.redirect(result.loadedContent.restoreFormUrl
        + '?formID=' + formID
        + '&error=' + JSON.stringify(error));
    } else {
      res.redirect(result.signResult.desc);
    }
  });
};

/**
 * Save signed content to redis, redirect to original url for further form submit
 * https://github.com/e-government-ua/i/issues/1374
 * @param req
 * @param res
 */
module.exports.callback = function (req, res) {
  var sHost = req.region.sHost
    , sURL = sHost + '/'
    , formID = req.session.formID
    , codeValue = req.query.code
    , accessToken = req.session.access.accessToken
    , type = req.session.type
    , userService = authProviderRegistry.getUserService(type);


  if (!codeValue) {
    codeValue = req.query['amp;code'];
  }

  /**
   * Load previously saved form from redis
   * @param callback
   */
  function loadFormAsync(callback) {
    loadContent(formID, sURL, function (error, response, body) {
      if (error) {
        callback(error, null);
      } else {
        callback(null, body);
      }
    });
  }

  /**
   * Load content that was signed on previous stage (signContent)
   * @param formData
   * @param callback
   */
  function downloadSignedContentAsync(formData, callback) {
    userService.downloadSignedContent(accessToken, codeValue, function (error, result) {
      callback(error, {signedContent: result, formData: formData});
    });
  }


  function saveSignedContentToRedisAsync(result, callback) {
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
      }
    }, sHost);
  }


  function handleResult(err, result) {
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
    downloadSignedContentAsync,
    saveSignedContentToRedisAsync
  ], handleResult);
};


/**
 * Common method for getting data from redis by ID
 * @param contentID
 * @param sURL
 * @param callback
 */
function loadContent(contentID, sURL, callback) {
  request.get({
    // url: sURL + 'service/object/file/download_file_from_redis_bytes',
    url: sURL + 'service/object/file/getProcessAttach',
    auth: getAuth(),
    qs: {
      sKey: contentID,
      sID_StorageType : 'Redis'
    },
    json: true
  }, callback);

  function getAuth() {
    var options = getOptions();
    return {
      'username': options.username,
      'password': options.password
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
  }
}



