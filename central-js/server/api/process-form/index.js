var express = require('express')
  , router = express.Router()
  , form = require('./form.controller')
  , auth = require('../../auth/auth.service.js')
  , region = require('../../components/region')
  , endpoint = require('../../components/endpoint');

router.get('/',
  endpoint.assertQueryParams('nID_Server', 'sID_BP_Versioned'),
  auth.isAuthenticated(),
  region.searchForHost(),
  form.index);

router.post('/', auth.isAuthenticated(), form.submit);
router.get('/sign', auth.isAuthenticated(), form.signForm);
router.use('/sign/callback', auth.isAuthenticated(), form.signFormCallback);
router.get('/sign/check', auth.isAuthenticated(), region.searchForHost(), form.signCheck);
router.post('/save', auth.isAuthenticated(), form.saveForm);
router.get('/load', auth.isAuthenticated(), form.loadForm);
router.post('/scansUpload', auth.isAuthenticated(), form.scanUpload);

module.exports = router;
