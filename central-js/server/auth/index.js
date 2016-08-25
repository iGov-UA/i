var express = require('express')
  , router = express.Router()
  , passport = require('passport')
  , request = require('request')
  , config = require('../config/environment')
  , authService = require('./auth.service')
  , authController = require('./auth.controller')
  , authProviderRegistry = require('./auth.provider.registry');

// Registering oauth2 strategies
require('./bankid/bankid.passport').setup(config, authProviderRegistry);
require('./bankid-nbu/bankid.passport').setup(config, authProviderRegistry);
require('./email/email.passport').setup(config, authProviderRegistry);

//Mock bankId process
router.use('/bankID', require('./bankid-mock'));
router.use('/bankID', require('./bankid'));
router.use('/bankid-nbu', require('./bankid-nbu'));

//Mock eds process
router.use('/eds', require('./eds-mock'));
router.use('/eds', require('./eds'));
router.use('/mpbds', require('./mpbds'));
router.use('/email', require('./email'));

if(config.hasSoccardAuth()){
  require('./soccard/soccard.passport').setup(config, authProviderRegistry);
  router.use('/soccard', require('./soccard'));
}

//Registering cookies for mocking
router.use('/isAuthenticated', require('./config-mock'));
router.get('/isAuthenticated', authService.isAuthenticated(), authController.isAuthenticated);
router.post('/logout', authService.isAuthenticated(), authController.logout);

module.exports = router;
