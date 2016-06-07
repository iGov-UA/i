/**
 * Created by Igor on 5/13/2016.
 */
'use strict';

var config = require('../../config/environment'),
    express = require('express');

var router = express.Router();

router.get('/', function (req, res, next) {
  if(config.bTest){
    res.cookie('bServerTest', config.bTest);
  }else{
    res.clearCookie('bServerTest');
  }
  next();
});

module.exports = router;



