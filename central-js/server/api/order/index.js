var express = require('express');
var router = express.Router();
var order = require('./order.controller');
var auth = require('../../auth/auth.service.js');

router.get('/search/:nID', order.searchOrderBySID);
router.post('/setTaskAnswer', order.setTaskAnswer);

module.exports = router;
