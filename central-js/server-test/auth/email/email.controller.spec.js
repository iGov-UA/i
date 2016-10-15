'use strict';

require('./../../api/subject/subject.service.nock.js');
require('./email.service.nock.js');

var should = require('should')
  , appTest = require('../../app.spec.js');

describe('auth with email service tests', function () {
  it('should respond with 200 and session', function (done) {
    appTest.loginWithEmail(function (error, loginAgent) {
      if(error){
        done(error);
      } else {
        done();
      }
    });
  });
});
