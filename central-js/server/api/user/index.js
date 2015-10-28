var express = require('express');
var router = express.Router();
var user = require('./user.controller');
var auth = require('../../auth/auth.service.js');

router.get('/', auth.isAuthenticated(), user.index);
router.get('/fio', auth.isAuthenticated(), user.fio);

module.exports = router;
