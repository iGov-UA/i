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

module.exports.authorize = function (req, res) {
  activiti.sendGetRequest(req, res, '/access/verifyContactEmail', {
    sQuestion: req.session.prepare.data.email,
    sAnswer: req.session.prepare.data.code
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

  activiti.sendGetRequest(req, res, '/access/verifyContactEmail', {
    sQuestion: req.session.prepare.data.email,
    sAnswer: req.session.prepare.data.code
  }, verifiedCallback(function (error, verified) {
    if (error) {
      res.status(401).send(error);
    } else {
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
