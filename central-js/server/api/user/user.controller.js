var authProviderRegistry = require('../../auth/auth.provider.registry')
  , activiti = require('../../components/activiti')
  , Admin = require('../../components/admin')
  , logger = require('../../components/logger').createLogger(module)
  , errors = require('../../components/errors');


function removeEmptyFields(customer) {
  iterateObj(customer, function (value, key) {
    return value === '';
  });
  return customer;
}

function iterateObj(obj, filter) {
  Object.keys(obj).forEach(function (key) {
    if (typeof obj[key] === 'object') {
      return iterateObj(obj[key], filter);
    }
    var isFiltered = filter(obj[key], key);
    if (isFiltered) {
      delete obj[key];
    }
  });
}

var finishRequest = function (req, res, err, result, userService) {
  if (err) {
    logger.info("[tryCache] error on cache search", {err: err});
    res.status(err.code);
    res.send(err);
    res.end();
  } else {
    req.session.subject = result.subject;
    req.session.bAdmin = result.admin;

    var customer = userService.convertToCanonical(result.customer);
    customer.sUsedAuthType = req.session.type;
    var admin = result.admin;

    logger.info("[tryCache] user is found", {result: customer});

    if (Admin.isAdminInn(customer.inn)) {
      admin = {
        inn: customer.inn,
        token: Admin.generateAdminToken(customer.inn)
      };
    }
    removeEmptyFields(customer);
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
  res.send({
    firstName: account.firstName,
    middleName: account.middleName,
    lastName: account.lastName,
    subjectID: subjectID
  });
};

module.exports.tryCache = function (req, res, next) {
  var type = req.session.type;
  var userService = authProviderRegistry.getUserService(type);

  if (req.session.usercacheid) {
    logger.info("[tryCache] getting user from cache", {session_type: type});

    function processResult(error, response, body) {
      logger.info("[tryCache] process user info from cache", {session_type: type});
      var err;
      if (error) {
        err = {code: 500, message: 'Unknown error', nested: error};
      } else if (errors.isHttpError(response.statusCode)) {
        if (body.hasOwnProperty('code') && body.hasOwnProperty('message')) {
          err = {code: response.statusCode, message: 'External service error : ' + body.message, nested: body};
        } else {
          err = {code: response.statusCode, message: 'Unknown service error:' + body, nested: body};
        }
      }
      finishRequest(req, res, err, body, userService);
    }

    var callback = processResult;
    if (userService.decryptCallback) {
      logger.info("[tryCache] using decrypt callback", {session_type: type});
      callback = userService.decryptCallback(processResult);
    } else {
      logger.info("[tryCache] doesn't have decrypt callback", {session_type: type});
    }

    // activiti.get('/object/file/download_file_from_redis_bytes', {
    //   key: req.session.usercacheid
    // }, callback);
    activiti.get('/object/file/getProcessAttach', {
      sKey: req.session.usercacheid,
      sID_StorageType: 'Redis'
    }, callback);

  } else {
    //TODO error handling
    //TODO if no cache kill session and force authorization again ???
    logger.info("[tryCache] no cache. Go to get user from service", {session_type: type});
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
