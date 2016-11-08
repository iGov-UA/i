'use strict';

require('./../../api/subject/subject.service.nock');
require('./email.service.nock');

var should = require('should')
  , appTest = require('../../app.spec');

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
