var express = require('express');
var router = express.Router();
var controller = require('./answer.controller.js');
var auth = require('../../auth/auth.service.js');


router.get('/DFS/decrypted', auth.isAuthenticated(), controller.getDecrypted);
router.get('/DFS/decrypted/json', auth.isAuthenticated(), controller.getJSON);

module.exports = router;