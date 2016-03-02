'use strict';

require('./subject.service.nock');

var should = require('should')
  , subjectService = require('./subject.service');


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
