'use strict';

var uuid = require('node-uuid');

function prepareSession(oSession) {
  if (!oSession.customer || Object.keys(oSession.customer).length < 1) {
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
  console.log('sID_Session created='+sUid);

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
    console.log('sID_Session='+sID_Session);
    console.log(oData)
    if (oData) {
      req.session = oData

      delete global.mSession[sID_Session];
      console.log('[global.mSession]', global.mSession);
      res.redirect(sNewLocaion);
    } else {
      res.redirect(sNewLocaion);
    }
  } else {
    res.redirect(sNewLocaion);
  }
};