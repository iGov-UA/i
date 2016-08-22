var async = require('async')
  , bankidUtil = require('./../../auth/bankid/bankid.util')
  , bankidService = require('../../auth/bankid/bankid.service')
  , bankidNBUService = require('../../auth/bankid-nbu/bankid.service')
  , soccardService = require('../../auth/soccard/soccard.service')
  , emailService = require('../../auth/email/email.service')
  , userConvert = require('./user.convert')
  , activiti = require('../../components/activiti')
  , Admin = require('../../components/admin')
  , errors = require('../../components/errors');


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

    // сохранение признака для отображения надписи о необходимости проверки регистрационных данных, переданых от BankID
    if(type === 'bankid' || type === 'bankid-nbu'){
      customer.isAuthTypeFromBankID = true;
    } else {
      customer.isAuthTypeFromBankID = false;
    }

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
  if (type === 'bankid' || type === 'eds' || type === 'bankid-nbu') {
    if (req.session.usercacheid) {
      var callback = bankidUtil.decryptCallback(function (error, response, body) {
        var err = null;
        if (error) {
          err = {code : 500, message : 'Unknown error', nested : error};
        } else if (errors.isHttpError(response.statusCode)){
          if (body.hasOwnProperty('code') && body.hasOwnProperty('message')){
            err = {code : response.statusCode, message : 'External service error : ' + body.message, nested : body};
          } else {
            err = {code : response.statusCode, message : 'Unknown service error:' + body, nested : body};
          }
        }
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
  var type = req.session.type;
  if (type === 'bankid' || type === 'eds' || type === 'mpbds') {
    bankidService.syncWithSubject(req.session.access.accessToken, function (err, result) {
      finishRequest(req, res, err, result, type);
    });
  } else if (type === 'bankid-nbu') {
    bankidNBUService.syncWithSubject(req.session.access.accessToken, function (err, result) {
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
