'use strict';

var appTest = require('../../app.spec')
  , organServiceTest = require('./organ.service.test');

appTest
  .centralNock
  .get('/wf/service/subject/getSubjectOrgan')
  .query(true)
  .reply(200, organServiceTest.testData.subjectOrgan, {
    'content-type': 'application/json;charset=UTF-8'
  });

appTest
  .centralNock
  .get('/wf/service/subject/getSubjectHuman')
  .query(true)
  .reply(200, organServiceTest.testData.subjectHuman, {
    'content-type': 'application/json;charset=UTF-8'
  });
