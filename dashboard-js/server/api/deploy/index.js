/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

var express = require('express');
var controller = require('./deploy.controller');

var router = express.Router();

router.post('/setBP/:sFileName', controller.setBP);
router.get('/getBP/:sID', controller.getBP);
router.get('/getListBP', controller.getListBP);
router.delete('/removeListBP', controller.removeListBP);

module.exports = router;
