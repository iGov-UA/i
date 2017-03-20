var express = require('express');
var router = express.Router();
var org = require('./org.controller.js');

router.get('/', org.getOrganization);

module.exports = router;
