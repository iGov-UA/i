'use strict';

var express = require('express')
  , router = express.Router()
  , sign = require('./sign.controller');

router.get('/sign', sign.signContent);
router.use('/sign/callback', sign.callback);

module.exports = router;