'use strict';

var express = require('express')
  , router = express.Router()
  , emailController = require('./email.controller.js')
  , authService = require('./../auth.service');


router.post('/verifyContactEmail', emailController.verifyContactEmail);
router.post('/verifyContactEmailAndCode', authService.isAuthenticationInProgress('email'), emailController.verifyContactEmailAndCode);
router.post('/editFio', authService.isAuthenticationInProgress('email'), emailController.editFio);
router.get('', authService.isAuthenticationInProgress('email'), emailController.authorize);

module.exports = router;
