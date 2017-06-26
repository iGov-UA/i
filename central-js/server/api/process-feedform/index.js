var express = require('express')
  , router = express.Router()
  , feedform = require('./feedform.controller')
  , auth = require('../../auth/auth.service.js')
  , region = require('../../components/region')
  , endpoint = require('../../components/endpoint')
  , scansMock = require('../user-mock/scans-mock.controller.js');


router.post('/', feedform.submit);
router.post('/getTaskData', feedform.getTaskData);

module.exports = router;
