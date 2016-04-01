var async = require('async')
  , bankidUtil = require('./../../auth/bankid/bankid.util')
  , bankidService = require('../../auth/bankid/bankid.service.js')
  , soccardService = require('../../auth/soccard/soccard.service.js')
  , emailService = require('../../auth/email/email.service.js')
  , userConvert = require('./user.convert')
  , activiti = require('../../components/activiti');

var finishRequest = function (req, res, err, result, type) {
  if (err) {
    res.status(err.code);
    res.send(err);
    res.end();
  } else {
    req.session.subject = result.subject;
    req.session.bAdmin = result.admin;
    res.send({
      customer: userConvert.convertToCanonical(type, result.customer),
      admin: result.admin
    });
    res.end();
  }
};

module.exports.fio = function (req, res) {
  var account = req.session.account;
  res.send({firstName: account.firstName, middleName: account.middleName, lastName: account.lastName});
};

module.exports.tryCache = function (req, res, next) {
  var type = req.session.type;
  if (type === 'bankid' || type === 'eds') {
    if (req.session.usercacheid) {

      var callback = bankidUtil.decryptCallback(function (error, response, body) {
        var err = null;
        //TODO error handling
        //TODO if no cache kill session and force authorization again ???
        finishRequest(req, res, err, body, type);
      });

      activiti.get('/object/file/download_file_from_redis_bytes', {
        key: req.session.usercacheid
      }, callback);
    } else {
      next();
    }
  }
};

module.exports.index = function (req, res) {
  //var config = require('../../config/environment');
  var config = require('../../config');

  var type = req.session.type;
  if (type === 'bankid' || type === 'eds') {
    bankidService.syncWithSubject(req.session.access.accessToken, function (err, result) {
      finishRequest(req, res, err, result, type);
    });
  } else if (type === 'soccard') {
    soccardService.syncWithSubject(req.session.access.accessToken, function (err, result) {
      finishRequest(req, res, err, result, type);
    });
  } else if (type === 'email') {
    emailService.syncWithSubject(req.session.access.email, function (err, result) {
      if (!result.customer.firstName) {
        result.customer.firstName = req.session.account.firstName;
      }
      if (!result.customer.lastName) {
        result.customer.lastName = req.session.account.lastName;
      }
      if (!result.customer.middleName) {
        result.customer.middleName = req.session.account.middleName;
      }
      if (!result.customer.email) {
        result.customer.email = req.session.access.email;
      }
      finishRequest(req, res, err, result, type);
    });
  }
};
