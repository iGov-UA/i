var url = require('url');
var async = require('async');
var request = require('request');
var authProviderRegistry = require('../../auth/auth.provider.registry');
var activiti = require('../../components/activiti');
var uploadFileService = require('../uploadfile/uploadfile.service');
var errors = require('../../components/errors');


/**
 * Load previously saved content from redis, sign it, and call(callback) service for saving signed content back to redis
 * @param req
 * @param res
 */
module.exports.signContent = function (req, res) {
  var formID = req.session.formID,
      sHost = req.region.sHost,
      sURL = sHost + '/',
      type = req.session.type,
      userService = authProviderRegistry.getUserService(type);

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
    var contentToSign = [];

    uploadFileService.downloadBuffer(formID, function (error, response, buffer) {
      contentToSign.push({
        name: 'file',
        options: {
          filename: buffer.formData.contentToSign.contentName
        },
        buffer: buffer.formData.contentToSign.contentValue
      });

      objectsToSign = objectsToSign.concat(contentToSign);
      callbackAsync(null, result);
    }, sHost);
  }

  function signContentAsync(result, callbackAsync) {
    userService.signFiles(
        req.session.access.accessToken,
        url.resolve(
            originalURL(req, {}),
            '/api/process-form/signMultiple/callback?nID_Server=' + req.query.nID_Server
        ),
        objectsToSign,
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
 * Common method for getting data from redis by ID
 * @param contentID
 * @param sURL
 * @param callback
 */
function loadContent(contentID, sURL, callback) {
  request.get({
    url: sURL + 'service/object/file/download_file_from_redis_bytes',
    auth: getAuth(),
    qs: {
      key: contentID
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



