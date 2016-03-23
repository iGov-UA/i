'use strict';

var appTest = require('../../app.spec')
  , subjectServiceTest = require('./subject.service.test.js');

var headers = {
  'content-type': 'application/json;charset=UTF-8'
};

appTest
  .centralNock
  .get('/wf/service/subject/syncSubject')
  .query(true)
  .reply(200, subjectServiceTest.testData.subject, headers);

appTest
  .centralNock
  .get('/wf/service/subject/getSubjectOrgan')
  .query(true)
  .reply(200, subjectServiceTest.testData.subjectOrgan, headers);

appTest
  .centralNock
  .get('/wf/service/subject/getSubjectHuman')
  .query(true)
  .reply(200, subjectServiceTest.testData.subjectHuman, headers);

appTest
  .centralNock
  .get('/wf/service/subject/getServer')
  .query(true)
  .reply(200, subjectServiceTest.testData.sHost, headers);
