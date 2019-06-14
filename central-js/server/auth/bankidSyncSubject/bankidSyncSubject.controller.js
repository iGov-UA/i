'use strict';

var authService = require('../auth.service');
var uuid = require('node-uuid');

function prepareSession(oSession) {
  if (!oSession.customer) {
    oSession.customer = {
      firstName: oSession.account.firstName,
      middleName: oSession.account.middleName,
      lastName: oSession.account.lastName
    };
  }
  
  delete oSession.usercacheid;
  oSession.access = {};

  return oSession;
}

module.exports.bankidSyncSubject = function (req, res) {
  var oSession = prepareSession(req.body);
  var sUid = uuid.v1({
    msecs: new Date().getTime()
  });

  if (!global.mSession) {
    global.mSession = {};
  }
  global.mSession[sUid] = oSession;

  res.send({
    sID_Session: sUid
  });
  res.end();
};

module.exports.restoreSession = function (req, res) {
  var sBackURL = req.header('Referer') || '/';
  var reg = new RegExp("((&)*sID_Session=([^&]*))","g");
  var sNewLocaion = sBackURL.replace(reg, '');

  var sID_Session = req.query.sID_Session;
  if (global.mSession) {
    var oData = global.mSession[sID_Session];
    console.log('========RESTORE==========')
    console.log(oData)
    if (oData) {
      req.session = authService.createSessionObject(oData.type || 'bankid', oData, 
        oData.access);
      delete req.session.prepare;

      res.redirect(sNewLocaion);
    } else {
      res.redirect(sNewLocaion);
    }
  } else {
    res.redirect(sNewLocaion);
  }
};