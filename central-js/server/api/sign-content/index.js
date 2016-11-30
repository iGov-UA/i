'use strict';

var express = require('express')
  , router = express.Router()
  , sign = require('./sign.controller')
  , decrypt = require('./decrypt.controller')
  , auth = require('../../auth/auth.service.js');

router.get('/sign', sign.signContent);
router.use('/sign/callback', sign.callback);
router.get('/decrypt', auth.isAuthenticated(), decrypt.decryptContent);
router.use('/decrypt/callback', auth.isAuthenticated(), decrypt.callback);

module.exports = router;