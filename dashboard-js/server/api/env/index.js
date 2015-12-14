'use strict';

var express = require('express');
var router = express.Router();
var config = require('../../config/environment');

router.get('/get-env-config', function(req, res) {
   res.setHeader('Last-Modified', (new Date()).toUTCString());
   res.status(200).json({
       bTest: config.bTest
   });
});


module.exports = router;
