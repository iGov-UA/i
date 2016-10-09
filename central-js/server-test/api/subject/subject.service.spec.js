'use strict';

require('./subject.service.nock.js');

var should = require('should')
  , subjectService = require('./../../../server/api/subject/subject.service.js');


describe('organ.service service tests', function () {
  it('should respond with 200 and without errors', function (done) {
    subjectService.getSubjectOrgan('1', function (error, response, body) {
      console.log(JSON.stringify(body));
      if (error) {
        done(error)
      } else {
        done();
      }
    });
  });

  it('should respond with 200 and without errors', function (done) {
    subjectService.getSubjectHuman('1', function (error, response, body) {
      console.log(JSON.stringify(body));
      if (error) {
        done(error)
      } else {
        done();
      }
    });
  });
});
