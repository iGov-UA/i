'use strict';

var express = require('express');
var router = express.Router();
var sign = require('./sign.controller');

router.get('/sign', sign.signContent);

module.exports = router;