/**
 * Created by Igor on 5/12/2016.
 */
var async = require('async')
  , authProviderRegistry = require('../../auth/auth.provider.registry')
  , Admin = require('../../components/admin/index')
  , mock = require('./user.data.js');

var finishRequest = function (req, res, err, result, userService) {
  if (err) {
    res.status(err.code);
    res.send(err);
    res.end();
  } else {
    req.session.subject = result.subject;
    req.session.bAdmin = result.admin;

    var customer = userService.convertToCanonical(result.customer);
    var admin = result.admin;
    if (Admin.isAdminInn(customer.inn)) {
      admin = {
        inn: customer.inn,
        token: Admin.generateAdminToken(customer.inn)
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
          var userService = authProviderRegistry.getUserService(type);
          finishRequest(req, res, err, body, userService);
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
    var userService = authProviderRegistry.getUserService(type);
    var userKey = userService.getUserKeyFromSession(req.session);

    userService.syncWithSubject(userKey, function (err, result) {
      if (userService.mergeFromSession) {
        userService.mergeFromSession(result, req.session);
      }
      finishRequest(req, res, err, result, userService);
    });
  } else {
    next();
  }
};
