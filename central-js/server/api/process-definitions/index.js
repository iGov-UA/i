var express = require('express')
  , router = express.Router()
  , endpoint = require('../../components/endpoint')
  , processDefinitions = require('./index.controller');

router.get('/', endpoint.assertQueryParams('nID_Server'), processDefinitions.index);

module.exports = router;
