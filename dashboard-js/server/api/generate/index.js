'use strict';

var express = require('express');
var controller = require('./generate.controller');

var router = express.Router();

router.post('/pdf/download', controller.convertToPDFAndDownload);
router.post('/pdf', controller.convertToPDFBase64);
router.post('/pdf/encodeBase64', controller.convertToPDFBase64ThroughJava);
router.post('/pdf/encodeBase64Mime', controller.convertToPDFBase64MimeThroughJava);
router.post('/pdf/decodeBase64/:isMime', controller.getBase64DecodedFileThroughJava);

module.exports = router;

