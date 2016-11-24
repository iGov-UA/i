'use strict';

var request = require('request')
  , async = require('async')
  , config = require('../../config/environment')
  //, config = require('../../config')
  , activiti = require('../../components/activiti')
  , authService = require('../auth.service')
  , emailService = require('./email.service')
  , errors = require('../../components/errors');

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

module.exports.authorize = function (req, res) {
  var email = req.session.prepare.data.email;
  var code = req.session.prepare.data.code;

  if(!email){
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "email should be specified in session prepare"));
    return;
  }

  if(!code){
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "code should be specified in session prepare"));
    return;
  }

  activiti.sendGetRequest(req, res, '/access/verifyContactEmail', {
    sQuestion: email,
    sAnswer: code
  }, verifiedCallback(function (error, verified) {
    if (error) {
      res.status(401).send(error);
    } else {
      var prepare = req.session.prepare;
      delete req.session.prepare;

      req.session = authService.createSessionObjectFromPrepare(prepare);
      res.redirect(prepare.data.link);
    }
  }));
};

module.exports.editFio = function (req, res) {
  var firstName = req.body.firstName;
  var lastName = req.body.lastName;
  var middleName = req.body.middleName;

  var email = req.session.prepare.data.email;
  var code = req.session.prepare.data.code;

  if(!email){
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "email should be specified in session prepare"));
    return;
  }

  if(!code){
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "code should be specified in session prepare"));
    return;
  }

  activiti.sendGetRequest(req, res, '/access/verifyContactEmail', {
    sQuestion: email,
    sAnswer: code
  }, verifiedCallback(function (error, verified) {
    if (error) {
      res.status(401).send(error);
    } else {
      req.session.prepare.data.user.customer = {};
      var customer = req.session.prepare.data.user.customer;

      if (firstName) {
        customer.firstName = firstName;
      }
      if (lastName) {
        customer.lastName = lastName;
      }
      if (middleName) {
        customer.middleName = middleName;
      }

      res.status(200).send({verified: true, edited: true});
    }
  }));
};

module.exports.verifyContactEmail = function (req, res) {
  var email = req.body.email;
  var link = req.body.link;

  if(!email){
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "email should be specified"));
    return;
  }

  if(!link){
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "link should be specified"));
    return;
  }

  emailService.verifyContactEmail(req.body.email, httpCallback(function (error, result) {
    if (error) {
      res.status(401).send(error);
    } else {
      req.session.prepare = authService.createPrepareSessionObject('email', {
        email: email,
        link: link
      });
      res.status(200).send(result);
    }
  }));
};

module.exports.verifyContactEmailAndCode = function (req, res) {
  var email = req.body.email;
  var code = req.body.code;

  if(!code){
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "code should be specified"));
    return;
  }

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
        var customer = user.customer;

        req.session.prepare.data.code = code;
        req.session.prepare.data.user = user;
        req.session.prepare.data.access = {
          email: email,
          code: code
        };
        res.status(200).send({
          verified: true,
          firstName: customer.firstName,
          lastName: customer.lastName,
          middleName: customer.middleName
        });
      }
    });
};
