'use strict';

var appTest = require('../../app.spec')
  , syncSubjectServiceTest = require('./syncSubject.service.test');

var headers = {
  'content-type': 'application/json;charset=UTF-8'
};

appTest
  .centralNock
  .get('/wf/service/subject/syncSubject')
  .query(true)
  .reply(200, syncSubjectServiceTest.testData.subject, headers);
