'use strict';

var express = require('express')
  , bankidController = require('./bankid.controller');

var router = express.Router();

router.get('/', bankidController.authenticate);
router.get('/callback', bankidController.token);

module.exports = router;
