'use strict';
var request = require('request'),
    masterPassAuth = require('./mp.service'),
    async = require('async');

module.exports.walletOperations = function (req, res) {
  var auth = masterPassAuth.getUserAuth();
  var params = req.body.body;

  var callback = function (error, response, body) {
    if(!error) {
      res.send(body);
      res.end();
    } else {
      res.send(error.message);
      res.end();
    }
  };

  request.post({
    "url": "https://walletmc.ipay.ua/",
    "headers": {"Content-type": "application/json; charset=utf-8"},
    "json": {
      "request": {
        "auth": auth,
        "action": req.body.action,
        "body": params
      }
    }
  }, callback);
};

module.exports.checkUser = function (req, res) {
  var auth = masterPassAuth.getUserAuth();
  var params = req.body.body;

  async.waterfall([
    walletCheck,
    showCardListOrRegister
  ], function (err, result, body) {
    if(!err) {
      res.send(body);
      res.end();
    } else {
      res.send(err.message);
      res.end();
    }
  });
  function walletCheck(callback) {
    request.post({
      "url": "https://walletmc.ipay.ua/",
      "headers": {"Content-type": "application/json; charset=utf-8"},
      "json": {
        "request": {
          "auth": auth,
          "action": req.body.action,
          "body": params
        }
      }
    }, function (err, res, body) {
      if(!err) {
        callback(null, body)
      } else {
        callback(error, null);
      }
    });

  }
  function showCardListOrRegister(result, callback) {
    var action;
    if(result.response.user_status === 'notexists') {
      action = 'RegisterByURL';
    } else if(result.response.user_status === 'invite') {
      action = 'InviteByURL';
    } else if(result.response.user_status === 'exists') {
      action = 'List';
    }

    request.post({
      "url": "https://walletmc.ipay.ua/",
      "headers": {"Content-type": "application/json; charset=utf-8"},
      "json": {
        "request": {
          "auth": auth,
          "action": action,
          "body": params
        }
      }
    }, function (err, response, body) {
      if(!err) {
        res.send(body);
        res.end();
      } else {
        res.send(err.message);
        res.end();
      }
    });
  }
};

module.exports.verify3DSCallback = function (req, res) {
  var auth = masterPassAuth.getUserAuth(),
      params = {"pmt_id": req.query.id, "md": req.body.MD, "pares": req.body.PaRes},
      callbackUrl = req.query.url;

  async.waterfall([
    check3DSAnswer,
    checkoutOrReturn
  ], function (err, result, body) {
    if(!err) {
      res.send(body);
      res.end();
    } else {
      res.send(err.message);
      res.end();
    }
  });
  function check3DSAnswer(callback) {
    request.post({
      "url": "https://walletmc.ipay.ua/",
      "headers": {"Content-type": "application/json; charset=utf-8"},
      "json": {
        "request": {
          "auth": auth,
          "action": "PaymentVerify3DS",
          "body": params
        }
      }
    }, function (err, response, body) {
      if(!err) {
        callback(null, body)
      } else {
        callback(error, null);
      }
    });
  }
  function checkoutOrReturn(result, callback) {
    if(result.response.pmt_status == 1) {
      request.post({
        "url": "https://walletmc.ipay.ua/",
        "headers": {"Content-type": "application/json; charset=utf-8"},
        "json": {
          "request": {
            "auth": auth,
            "action": 'PaymentSale',
            "body": {"pmt_id": result.response.pmt_id, "invoice": result.response.invoice, "guid": masterPassAuth.createGuid()}
          }
        }
      }, function (err, response, body) {
        if(!err) {
          res.redirect(callbackUrl + '?status=' + body.response.pmt_status + '&pmt_id=' + body.response.pmt_id);
          res.end();
        } else {
          res.send(err.message);
          res.end();
        }
      });
    } else {
      res.redirect(callbackUrl)
    }
  }
};

module.exports.createSaleCancelPayment = function (req, res) {
  var auth = masterPassAuth.getUserAuth();
  var params = req.body.body;
  params.guid = masterPassAuth.createGuid();

    request.post({
      "url": "https://walletmc.ipay.ua/",
      "headers": {"Content-type": "application/json; charset=utf-8"},
      "json": {
        "request": {
          "auth": auth,
          "action": req.body.action,
          "body": params
        }
      }
    }, function (err, response, body) {
      if(!err) {
        res.send(body);
        res.end();
      } else {
        res.send(err.message);
        res.end();
      }
    });
};
