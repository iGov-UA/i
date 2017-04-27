'use strict';

var express = require('express');
var controller = require('./process.controller');

var router = express.Router();

router.get('/', controller.index);
router.get('/getLoginBPs', controller.getLoginBPs);
router.get('/getBPs_ForExport', controller.getBPs_ForExport);


module.exports = router;
