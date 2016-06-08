/**
 * Created by Igor on 5/12/2016.
 */
var async = require('async')
  , bankidService = require('../../auth/bankid/bankid.service.js')
  , soccardService = require('../../auth/soccard/soccard.service.js')
  , emailService = require('../../auth/email/email.service.js')
  , userConvert = require('../user/user.convert')
  , activiti = require('../../components/activiti')
  , Admin = require('../../components/admin/index')
  , mock = require('./user.data.js');

var finishRequest = function (req, res, err, result, type) {
  if (err) {
    res.status(err.code);
    res.send(err);
    res.end();
  } else {
    req.session.subject = result.subject;
    req.session.bAdmin = result.admin;

    var customer = userConvert.convertToCanonical(type, result.customer);
    var admin = result.admin;
    if (Admin.isAdminInn(customer.inn)) {
      admin = {
        inn: customer.inn,
        token: Admin.generateAdminToken()
      };
    }
    res.send({
      customer: customer,
      admin: admin
    });
    res.end();
  }
};

module.exports.fio = function (req, res, next) {
  if (mock.isMockEnabled(req.cookies)) {
    var account = req.session.account;
    res.send({firstName: account.firstName, middleName: account.middleName, lastName: account.lastName});
  } else {
    next();
  }
};

module.exports.tryCache = function (req, res, next) {
  if (mock.isMockEnabled(req.cookies)) {
    var type = req.session.type;
    if (type === 'bankid' || type === 'eds') {
      if (req.session.usercacheid) {

        var callback = function (error, body) {
          var err = null;
          finishRequest(req, res, err, body, type);
        };

        function getUserMock() {
          return mock.user;
        }

        async.waterfall([
          function (callback) {
            return callback(null, getUserMock());
          }
        ], function (error, result) {
          callback(error, result);
        });

      } else {
        next();
      }
    }
  } else {
    next();
  }
};

module.exports.index = function (req, res, next) {

  if (mock.isMockEnabled(req.cookies)) {
    var type = req.session.type;
    if (type === 'bankid' || type === 'eds' || type === 'mpbds') {
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
  } else {
    next();
  }
};
