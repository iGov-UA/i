'use strict';

var request = require('request')
  , async = require('async')
  , config = require('../../config/environment')
  , activiti = require('../../components/activiti')
  , authService = require('../auth.service')
  , emailService = require('./email.service');

function httpCallback(asyncCallback) {
  return function (error, response, body) {
    if (error) {
      asyncCallback(error, null);
    } else {
      asyncCallback(null, body);
    }
  };
}

function verifiedCallback(asyncCallback) {
  return httpCallback(function (error, verifyingResult) {
    if (error) {
      asyncCallback(error, null)
    } else if (!verifyingResult.bVerified) {
      asyncCallback({msg: 'ошибка ввода кода подтверждения электронного адреса'}, null)
    } else {
      asyncCallback(null, true);
    }
  });
}

module.exports.verifyContactEmail = function (req, res) {
  emailService.verifyContactEmail(req.body.email, httpCallback(function (error, result) {
    if (error) {
      res.status(401).send(error);
    } else {
      res.status(200).send(result);
    }
  }));
};

module.exports.verifyContactEmailAndCode = function (req, res) {
  var email = req.body.email;
  var code = req.body.code;

  async.waterfall([
      function (asyncCallback) {
        activiti.sendGetRequest(req, res, '/access/verifyContactEmail', {
          sQuestion: email,
          sAnswer: code
        }, verifiedCallback(asyncCallback));
      },
      function (verifyied, asyncCallback) {
        emailService.syncWithSubject(email, function (error, result) {
          asyncCallback(error, result);
        });
      }
    ],
    function (error, user) {
      if (error) {
        res.status(401).send(error);
      } else {
        var access = {};
        req.session = authService.createSessionObject('email', user, access);
        res.status(200).send({authorized: true});
      }
    });
};
