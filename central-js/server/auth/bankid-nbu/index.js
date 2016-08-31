'use strict';

var express = require('express')
  , bankidNBUController = require('./bankid.controller');

var router = express.Router();

router.get('/', bankidNBUController.authenticate);
router.get('/callback', bankidNBUController.token);

module.exports = router;
