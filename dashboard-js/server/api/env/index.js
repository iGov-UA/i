'use strict';

var express = require('express');
var router = express.Router();

router.get('/get-env-config', function(req, res) {
   res.status(200).json({
       bTest: process.env.bTest
   });
});


module.exports = router;
