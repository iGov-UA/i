'use strict';

var express = require('express');
var router = express.Router();
var currencies = require('./index.controller');

router.get('/', currencies.getCurrencyList);

module.exports = router;
