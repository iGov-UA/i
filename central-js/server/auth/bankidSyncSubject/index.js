'use strict';

var express = require('express');
var router = express.Router();
var syncSubjectCtrl = require('./bankidSyncSubject.controller');

router.post('/', syncSubjectCtrl.bankidSyncSubject);

module.exports = router;