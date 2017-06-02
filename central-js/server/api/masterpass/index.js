var express = require('express');
var router = express.Router();
var mp = require('./mp.controller.js');

router.post('/', mp.walletOperations);
router.post('/checkUser', mp.checkUser);
router.post('/createSaleCancelPayment', mp.createSaleCancelPayment);
router.post('/verify3DSCallback', mp.verify3DSCallback);
router.get('/verifyPhoneNumber', mp.verifyPhoneNumber);
router.get('/confirmOtp', mp.confirmOtp);

module.exports = router;
