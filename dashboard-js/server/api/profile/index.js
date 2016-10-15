'use strict';

var express = require('express');
var controller = require('./profile.controller');

var router = express.Router();

router.get('/getSubjects/:saAccount/:nID_SubjectAccountType', controller.getSubjects);
router.post('/changePassword', controller.changePassword);


module.exports = router;
