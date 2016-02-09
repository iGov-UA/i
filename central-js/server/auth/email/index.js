'use strict';

var express = require('express');
var router = express.Router();
var emailController = require('./email.controller.js');


router.post('/verifyContactEmail', emailController.verifyContactEmail);
router.post('/verifyContactEmailAndCode', emailController.verifyContactEmailAndCode);

module.exports = router;
