var request = require('request')
  , async = require('async')
  , config = require('../../config/environment')
  , syncSubject = require('../../api/subject/subject.service.js')
  , userConvert = require('../../api/user/user.convert')
  , activiti = require('../../components/activiti')
  , errors = require('../../components/errors');

module.exports.verifyContactEmail = function (email, callback) {
  activiti.get('/access/verifyContactEmail', {sQuestion: email}, callback);
};

module.exports.getUser = function (nID, callback) {
  syncSubject.getSubjectHuman(nID, function (error, response, body) {
    if (error || body.error) {
      var errorResult = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR,
        body.error_description,
        error || body.error);
      callback(errorResult, null);
    } else if (body.code && body.message) {
      if (body.message.indexOf('Record not found') === 0) {
        callback(null, {});
      } else {
        var errorResult = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, body.message, body);
        callback(errorResult, null);
      }
    } else {
      callback(null, body);
    }
  });
};

var humanIDType = 2;

module.exports.syncWithSubject = function (email, done) {
  var self = this;

  async.waterfall([
      function (asyncCallback) {
        syncSubject.syncBySCodeAndHumanIDType(email, humanIDType, activiti.httpCallback(asyncCallback));
      },
      function (syncedResult, asyncCallback) {
        var nID = syncedResult.nID;
        var user = {
          customer: {},
          subject: syncedResult,
          admin: {}
        };

        self.getUser(nID, function (error, subjectHuman) {
          if (error) {
            asyncCallback(error, null);
          } else {
            user.customer = userConvert.convertToCanonical('email', subjectHuman);
            asyncCallback(null, user);
          }
        });
      }
    ],
    function (err, result) {
      done(err, result);
    });
};
