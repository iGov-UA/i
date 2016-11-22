var express = require('express');
var router = express.Router();
var controller = require('./answer.controller.js');


router.get('/DFS/decrypted', controller.getDecrypted);
router.get('/DFS/decrypted/json', controller.getJSON);

module.exports = router;