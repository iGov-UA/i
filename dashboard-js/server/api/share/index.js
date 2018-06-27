'use strict';

var express = require('express');
var router = express.Router();
var controller = require('./share.controller.js');


router.get('/getDocumentPDF', controller.getDocumentPDF);
router.get('/getDocumentImageFileVO', controller.getDocumentImageFileVO);
router.post('/setDocumentImageFileSign', controller.setDocumentImageFileSign);
router.post('/setDocumentImageFile', controller.setDocumentImageFile);
router.get('/getDocumentImageFileSigned', controller.getDocumentImageFileSigned);
// router.get('/getProcessAttach', controller.getProcessAttach);

module.exports = router;
