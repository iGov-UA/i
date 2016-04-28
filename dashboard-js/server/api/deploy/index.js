/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

var express = require('express');
var controller = require('./deploy.controller');

var router = express.Router();

router.post('/setBP', controller.setBP);
router.get('/getBP', controller.getBP);
router.get('/getListBP', controller.getListBP);
router.get('/removeListBP', controller.removeListBP);

module.exports = router;
