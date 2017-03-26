'use strict';

var express = require('express');
var controller = require('./index.controller');

var router = express.Router();

router.get('/createTask', controller.createTask);
router.post('/saveCreatedTask', controller.submitCreatedTask);

module.exports = router;
