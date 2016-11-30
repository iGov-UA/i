'use strict';

var express = require('express')
  , router = express.Router()
  , sign = require('./sign.controller')
  , decrypt = require('./decrypt.controller');

router.get('/sign', sign.signContent);
router.use('/sign/callback', sign.callback);
router.get('/decrypt', decrypt.decryptContent);
router.use('/decrypt/callback', decrypt.callback);

module.exports = router;