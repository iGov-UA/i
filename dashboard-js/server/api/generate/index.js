'use strict';

var express = require('express');
var controller = require('./generate.controller');

var router = express.Router();

router.post('/pdf/download', controller.convertToPDFAndDownload);
router.post('/pdf', controller.convertToPDFBase64);

module.exports = router;

