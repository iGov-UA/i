var authProviderRegistry = require('../../auth/auth.provider.registry')
  , activiti = require('../../components/activiti')
  , Admin = require('../../components/admin')
  , errors = require('../../components/errors');


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

module.exports.fio = function (req, res) {
  var account = req.session.account;
  var subjectID = req.session.subject.nID;
  //TODO remove subject from fio object
  res.send({firstName: account.firstName, middleName: account.middleName, lastName: account.lastName, subjectID: subjectID});
};

module.exports.tryCache = function (req, res, next) {
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);

  if (req.session.usercacheid) {
    if (userService.decryptCallback) {
      var callback = userService.decryptCallback(function (error, response, body) {
        var err = null;
        if (error) {
          err = {code: 500, message: 'Unknown error', nested: error};
        } else if (errors.isHttpError(response.statusCode)) {
          if (body.hasOwnProperty('code') && body.hasOwnProperty('message')) {
            err = {code: response.statusCode, message: 'External service error : ' + body.message, nested: body};
          } else {
            err = {code: response.statusCode, message: 'Unknown service error:' + body, nested: body};
          }
        }
        //TODO error handling
        //TODO if no cache kill session and force authorization again ???
        finishRequest(req, res, err, body, userService);
      });

      activiti.get('/object/file/download_file_from_redis_bytes', {
        key: req.session.usercacheid
      }, callback);
    }
  } else {
    next();
  }
};

module.exports.index = function (req, res) {
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);
  var userKey = userService.getUserKeyFromSession(req.session);

  userService.syncWithSubject(userKey, function (err, result) {
    if (userService.mergeFromSession) {
      userService.mergeFromSession(result, req.session);
    }
    finishRequest(req, res, err, result, userService);
  });
};
