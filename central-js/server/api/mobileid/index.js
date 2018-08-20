var express = require('express');
var router = express.Router();


var controller = require('./mobileid.controller.js');
//var auth = require('../../auth/auth.service.js');

router.post('/', controller.mobileid);

module.exports = router;
