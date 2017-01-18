'use strict';

var express = require('express');
var controller = require('./index.controller');

var router = express.Router();

router.post('/',controller.uploadFile);

module.exports = router;
