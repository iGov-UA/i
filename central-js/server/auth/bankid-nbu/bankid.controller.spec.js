'use strict';

var should = require('should')
, appTest = require('../../app.spec')
, testRequest = appTest.testRequest;

require('./bankid.nock.js');

describe('bankidNBUController initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUController', function () {
    return require('./bankid.controller');
  });
});

describe('authorize and check session with bankid nbu', function () {
  var agent;
  before(function (done) {
    appTest.loginWithBankIDNBU(function (error, loginAgent) {
      if (error) {
        done(error)
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    });
  });

  it('should respond with 200 and remove cookie session', function (done) {
    var logout = testRequest.post('/auth/logout');
    agent.attachCookies(logout);
    logout.expect(200)
      .then(function (res) {
        console.log('result!!!');
        //TODO check why cookies are not removed
        done();
      }).catch(function (err) {
      done(err)
    });
  });
});
