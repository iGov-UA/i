var request = require('request')
  , async = require('async')
  , soccardUtil = require('./soccard.util')
  , config = require('../../config/environment')
  , syncSubject = require('../../api/subject/subject.service.js')
  , errors = require('../../components/errors');

module.exports.getUser = function (accessToken, callback) {
  var infoURL = soccardUtil.getInfoURL(config);

  //GET /api/info HTTP/1.1
  //Host: test.kyivcard.com.ua
  //SocCard-API-Access-Token: NjM1NDhl...IwMDBlNWQ
  //SocCard-API-Version: 1.0
  //SocCard-API-Transaction-ID: de82c107e5b105c493239eb90f46c735
  //SocCard-API-Signature: ZGIwOTA...GUwZjZjZjI =
  var headers = {};

  soccardUtil.addAccessTokenHeader(headers, accessToken);
  soccardUtil.addAPIVersionTokenHeader(headers, config);
  soccardUtil.addTransactionHeader(headers);
  soccardUtil.addSignHeader(headers, config, headers['SocCard-API-Transaction-ID'],
    'GET', infoURL, '', ''
  );

  request.get({
    url: infoURL,
    headers: headers,
    json: true
  }, function (error, response, body) {
    if (error || body.error) {
      callback(
        errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR,
          body.error_description,
          error || body.error),
        null);
    } else {
      callback(null, {
        customer: body
      });
    }
  });

};

module.exports.syncWithSubject = function (accessToken, done) {
  async.waterfall([
      function (callback) {
        module.exports.getUser(accessToken, function (error, result) {
          callback(error, result);
        });
      },
      function (result, callback) {
        syncSubject.sync(result.customer.personNumber, function (error, response, body) {
          if (error) {
            callback(errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, body.error_description, error || body.error), null);
          } else {
            result.subject = body;
            callback(null, result);
          }
        });
      }
    ],
    function (err, result) {
      done(err, result);
    });
};
