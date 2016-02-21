'use strict';

require('./organ.service.nock');

var should = require('should')
  , organService = require('./organ.service');


describe('organ.service service tests', function () {
  it('should respond with 200 and without errors', function (done) {
    organService.getSubjectOrgan('1', function (error, response, body) {
      console.log(JSON.stringify(body));
      if (error) {
        done(error)
      } else {
        done();
      }
    });
  });

  it('should respond with 200 and without errors', function (done) {
    organService.getSubjectHuman('1', function (error, response, body) {
      console.log(JSON.stringify(body));
      if (error) {
        done(error)
      } else {
        done();
      }
    });
  });
});
