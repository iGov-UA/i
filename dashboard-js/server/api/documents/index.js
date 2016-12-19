'use strict';

var express = require('express');
var controller = require('./index.controller');

var router = express.Router();

router.get('/getDocumentStepRights', controller.getDocumentStepRights);
router.get('/getDocumentStepLogins', controller.getDocumentStepLogins);
router.get('/getProcessSubject', controller.getProcessSubject);
router.get('/getBPs', controller.getBPs);
router.get('/setDocument', controller.setDocument);

module.exports = router;
