/**
 * Created by Oleksii Khalikov on 04.08.2016.
 */
'use strict';

var express = require('express');
var router = express.Router();
var controller = require('./liqpay.controller.js');

router.get('/', controller.getRedirectPaymentLiqpay);

module.exports = router;
