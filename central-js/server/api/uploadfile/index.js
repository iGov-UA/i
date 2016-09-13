var express = require('express')
  , router = express.Router()
  , uploadfileController = require('./uploadfile.controller');

router.post('/', uploadfileController.uploadProxy);

module.exports = router;
