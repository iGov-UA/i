var async = require('async')
  , syncSubject = require('../../api/subject/subject.service')
  , activiti = require('../../components/activiti')
  , errors = require('../../components/errors');

module.exports.getUserKeyFromSession = function (session){
  return session.access.email;
};

module.exports.convertToCanonical = function (customer) {
  customer.isAuthTypeFromBankID = false;
  customer.type = 'physical';
  delete customer.nID;
  delete customer.sSB;
  delete customer.oSubject;
  if (!customer.middleName && customer.sSurname) {
    customer.middleName = customer.sSurname;
    delete customer.sSurname;
  }
  if (!customer.lastName && customer.sFamily) {
    customer.lastName = customer.sFamily;
    delete customer.sFamily;
  }
  if (!customer.firstName && customer.sName) {
    customer.firstName = customer.sName;
    delete customer.sName;
  }
  if (!customer.inn && customer.sINN) {
    customer.inn = customer.sINN;
    delete customer.sINN;
  }
  if (customer.sPassportSeria && customer.sPassportNumber) {
    customer.documents = [];
    customer.documents.push({
      "type": "passport",
      "series": customer.sPassportSeria,
      "number": customer.sPassportNumber
    });
    delete customer.sPassportSeria;
    delete customer.sPassportNumber;
  }
  return customer;
};

module.exports.mergeFromSession = function (result, session){
  if (!result.customer.firstName) {
    result.customer.firstName = session.account.firstName;
  }
  if (!result.customer.lastName) {
    result.customer.lastName = session.account.lastName;
  }
  if (!result.customer.middleName) {
    result.customer.middleName = session.account.middleName;
  }
  if (!result.customer.email) {
    result.customer.email = session.access.email;
  }
};

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
            user.customer = self.convertToCanonical('email', subjectHuman);
            asyncCallback(null, user);
          }
        });
      }
    ],
    function (err, result) {
      done(err, result);
    });
};
