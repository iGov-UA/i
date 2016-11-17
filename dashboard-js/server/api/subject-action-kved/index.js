/**
 * Created by Oleksii Khalikov on 29.07.2016.
 */
'use strict';

var express = require('express');
var router = express.Router();
var controller = require('./index.controller');

router.get('/', controller.getActionKVEDList);

module.exports = router;
