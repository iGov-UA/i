'use strict';

var authService = require('../auth.service');
var uuid = require('node-uuid');
//var request = require('request');

function expiresUserInMs() {
  return new Date(Date.now() + 1000 * 60 * 60);
}

function prepareSession(oSession) {
  oSession.customer = {
    firstName: oSession.account.firstName,
    middleName: oSession.account.middleName,
    lastName: oSession.account.lastName
  };
  delete oSession.usercacheid;

  return oSession;
}

/* module.exports.bankidSyncSubject = function (req, res) {
  if (req.query.bIsSelfInvoke) {
    var oSession = prepareSession(req.body);

    req.session = authService.createSessionObject(oSession.type || 'bankid', oSession, 
      oSession.access);

    res.send('Ok');
    res.end();
  } else {
    var oSession = req.body;
    var callback = function(error, response, body) {
      if (error) {
        res.statusCode = 400;
        res.send(error);
      } else {
        var aHeaderCookie = response.headers['set-cookie'];
        var sess = aHeaderCookie[0].split('express:sess=')[1].split(';')[0];
        var sig = aHeaderCookie[1].split('express:sess.sig=')[1].split(';')[0];
  
        var sUid = uuid.v1({
          msecs: new Date().getTime()
        });
    
        if (!global.mSession) {
          global.mSession = {};
        }
        global.mSession[sUid] = {
          'express:sess': sess,
          'express:sess.sig': sig
        }
  
        res.send({
          sID_Session: sUid
        });
        res.end();
      }
    };
    
    var sUrl = req.protocol + '://' + req.headers.host + req.baseUrl;
    request.post(sUrl, {
      qs: {
        sInn: oSession.subject.sID,
        bIsSelfInvoke: true
      },
      body: oSession,
      json: true
    }, callback);
  }
}; */

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

/* module.exports.restoreSession = function (req, res) {
  console.log('=========RESTORE========')
  console.log(req.query.sID_Session)
  console.log(global.mSession)
  if (global.mSession) {
    var oData = global.mSession[req.query.sID_Session];
    if (oData) {
      res.cookie('express:sess', oData['express:sess'], {expires: expiresUserInMs()});
      res.cookie('express:sess.sig', oData['express:sess.sig'], {expires: expiresUserInMs()});

      res.end('Session restored');
    } else {
      res.end('No data for sID: ' + req.query.sID_Session);
    }
  } else {
    res.end('No session data in session map');
  }
}; */

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