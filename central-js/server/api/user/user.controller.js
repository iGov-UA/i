var accountService = require('../../auth/bankid/bankid.service.js');
var soccardService = require('../../auth/soccard/soccard.service.js');
var userConvert = require('./user.convert');

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

module.exports.index = function (req, res) {
  var config = require('../../config/environment');
  var type = req.session.type;
  if (type === 'bankid' || type === 'eds') {
    accountService.syncWithSubject(req.session.access.accessToken, function (err, result) {
      finishRequest(req, res, err, result, type);
    });
  } else if (type === 'soccard') {
    soccardService.syncWithSubject(req.session.access.accessToken, function (err, result) {
      finishRequest(req, res, err, result, type);
    });
  }
};
