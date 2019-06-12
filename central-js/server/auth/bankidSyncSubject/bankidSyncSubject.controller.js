'use strict';

var authService = require('../auth.service')

function expiresUserInMs() {
  return new Date(Date.now() + 1000 * 60 * 60);
}

function prepareSession(oSession) {
  oSession.customer = {
    firstName: oSession.account.firstName,
    middleName: oSession.account.middleName,
    lastName: oSession.account.lastName
  };

  return oSession;
}

module.exports.bankidSyncSubject = function (req, res) {
    var oSession = prepareSession(req.body);

    req.session = authService.createSessionObject(oSession.type || 'bankid', oSession, 
        oSession.access);
    delete req.session.prepare;

    res.send({
        status: 'Ok'
    })
    res.end();
};

module.exports.restoreSession = function (req, res) {
  if (global.mSession) {
    var oData = global.mSession[req.query.sID_Session];
    if (oData) {
      res.cookie('express:sess', oData['express:sess'], {expires: expiresUserInMs()});
      res.cookie('express:sess.sig', oData['express:sess.sig'], {expires: expiresUserInMs()});

      res.send('Session restored');
    }
  } else {
    res.send('No session data in session map');
  }
};