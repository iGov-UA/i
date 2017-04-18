'use strict';

var express = require('express');
var controller = require('./generate.controller');

var router = express.Router();

router.post('/pdf', controller.convertToPDF);

module.exports = router;

