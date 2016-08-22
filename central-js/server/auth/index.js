var express = require('express');
var passport = require('passport');
var request = require('request');
var config = require('../config/environment');
//var config = require('../config');
var authService = require('./auth.service');
var authController = require('./auth.controller');
var router = express.Router();

// Registering oauth2 strategies
require('./bankid/bankid.passport').setup(config);

//Mock bankId process
router.use('/bankID', require('./bankid-mock'));

router.use('/bankID', require('./bankid'));

require('./bankid-nbu/bankid.passport').setup(config);
router.use('/bankid-nbu', require('./bankid-nbu'));

//Mock eds process
router.use('/eds', require('./eds-mock'));


router.use('/eds', require('./eds'));
router.use('/mpbds', require('./mpbds'));
router.use('/email', require('./email'));

if(config.hasSoccardAuth()){
  require('./soccard/soccard.passport').setup(config);
  router.use('/soccard', require('./soccard'));
}

//Registering cookies for mocking
router.use('/isAuthenticated', require('./config-mock'));

router.get('/isAuthenticated', authService.isAuthenticated(), authController.isAuthenticated);
router.post('/logout', authService.isAuthenticated(), authController.logout);

module.exports = router;
