'use strict';

var express = require('express');
var controller = require('./index.controller');

var router = express.Router();

router.get('/getDocumentStepRights', controller.getDocumentStepRights);
router.get('/getDocumentStepLogins', controller.getDocumentStepLogins);

module.exports = router;
