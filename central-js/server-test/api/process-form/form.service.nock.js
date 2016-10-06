'use strict';

var appTest = require('../../app.spec.js')
  , formServiceTest = require('./form.service.test.js');

var headers = {
  'content-type': 'application/json;charset=UTF-8'
};

appTest
  .regionMock
  .get('/wf/service/form/form-data')
  .query(true)
  .reply(200, formServiceTest.testData.forms.empty, headers);

appTest
  .regionMock
  .post('/wf/service/form/form-data')
  .query(true)
  .reply(200, formServiceTest.testData.forms.empty, headers);

