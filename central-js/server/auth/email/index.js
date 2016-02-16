'use strict';

var express = require('express');
var router = express.Router();
var emailController = require('./email.controller.js');
var authService = require('./../auth.service');

router.post('/verifyContactEmail', emailController.verifyContactEmail);
router.post('/verifyContactEmailAndCode', authService.isAuthenticationInProgress('email'), emailController.verifyContactEmailAndCode);
router.post('/editFio', authService.isAuthenticationInProgress('email'), emailController.editFio);
router.get('', authService.isAuthenticationInProgress('email'), emailController.authorize);

module.exports = router;
