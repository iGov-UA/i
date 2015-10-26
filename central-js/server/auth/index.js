var express = require('express');
var passport = require('passport');
var request = require('request');
var config = require('../config/environment');
var accountService = require('./bankid/bankid.service.js');
var auth = require('./auth.service');

// Registering oauth2 strategies
require('./bankid/bankid.passport').setup(config, accountService);
require('./soccard/soccard.passport').setup(config, accountService);

var router = express.Router();

router.use('/bankID', require('./bankid'));
router.use('/eds', require('./eds'));
router.use('/soccard', require('./soccard'));

router.get('/isAuthenticated', auth.isAuthenticated(), function(req,res){
    res.status(200);
    res.end();
});

router.post('/logout', auth.isAuthenticated(), function(req,res){
  res.session = null;
  res.status(200);
  res.end();
});

module.exports = router;
