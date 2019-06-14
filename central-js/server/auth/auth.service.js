'use strict';

var compose = require('composable-middleware');
var config = require('../config/environment');
var documents = require('../api/documents/documents.controller.js');

function isAuthenticationInProgress(type) {
  return compose().use(function (req, res, next) {
    if (req.session && req.session.prepare && req.session.prepare.type === type) {
      next();
    } else {
      res.status(401).send({msg: 'you should initiate authentication process at first'});
      res.end();
    }
  });
}

function isAuthenticated() {
  return compose().use(function (req, res, next) {
    if (req.session && !req.session.prepare && req.session.access && req.session.subject) {
      next();
    } else {
      res.status(401);
      res.end();
    }
  });
}

function isDocumentOwner() {
  return compose().use(isAuthenticated()).use(function (req, res, next) {
    documents.getDocumentInternal(req, res,
      function (error, response, body) {
        if (error) {
          res.status(response.statusCode);
          res.send(error);
          res.end();
        } else {
          try {
            var document = JSON.parse(body);
            //if (document.oSubject && document.oSubject.nID === req.session.subject.nID) {
            next();
            //} else {
            //	res.status(401).send({error: "User can have access only to his own documents"});
            //}
          } catch (e) {
            res.status(404).send({error: "There is no such document"});
          }

        }
      });
  });
}

function createPrepareSessionObject(type, data){
  return {
    type : type,
    data : data
  }
}

function createSessionObjectFromPrepare(prepare){
  return createSessionObject(prepare.type, prepare.data.user, prepare.data.access);
}

function createSessionObject(type, user, access) {
  return {
    type: type,
    account: {
      firstName: user.customer.firstName,
      middleName: user.customer.middleName,
      lastName: user.customer.lastName
    },
    subject: user.subject,
    access: access,
    usercacheid: user.usercacheid
  }
}

exports.isAuthenticated = isAuthenticated;
exports.isAuthenticationInProgress = isAuthenticationInProgress;
exports.isDocumentOwner = isDocumentOwner;
exports.createSessionObject = createSessionObject;
exports.createPrepareSessionObject = createPrepareSessionObject;
exports.createSessionObjectFromPrepare = createSessionObjectFromPrepare;
