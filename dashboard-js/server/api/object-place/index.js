/**
 * Created by Oleksii Khalikov on 01.08.2016.
 */
'use strict';

var express = require('express');
var router = express.Router();
var controller = require('./index.controller');

router.get('/', controller.getObjectPlaceUAList);

module.exports = router;
