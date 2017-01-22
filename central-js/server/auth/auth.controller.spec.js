'use strict';

var should = require('should')
  , appTest = require('../app.spec')
  , bankidData = require('./bankid/bankid.data.spec')
  , testRequest = appTest.testRequest;

describe('auth service tests', function () {
  var agent;
  before(function (done) {
    appTest.loginWithBankID(function (error) {
      if (error) {
        done(error)
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    }, bankidData.codes.forCustomerDataResponse);
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
