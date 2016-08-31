'use strict';

var should = require('should')
  , appTest = require('../../app.spec')
  , testRequest = appTest.testRequest;

require('../subject/subject.service.nock');
require('../uploadfile/upload.nock');
require('./form.service.nock');


describe('GET /api/process-form', function () {
  var url = '/api/process-form';
  var agent;
  before(function (done) {
    appTest.loginWithBankID(done, function (loginAgent) {
      agent = loginAgent;
    });
  });

  it('should respond with 400 if no nID_Server', function (done) {
    appTest.tests.testGETInputParamsAbsence(url, agent, done);
  });

  it('should respond with 400 if no sID_BP_Versioned', function (done) {
    appTest.tests.testGETInputParamsAbsence(url, agent, done);
  });

  it('should respond with 401 if no authorization', function (done) {
    appTest.tests.testGETUnathorized(url, done);
  });

  it('should respond with 200', function (done) {
    appTest.tests.testGET(url + '?nID_Server=1&sID_BP_Versioned=1', agent).expect(200).then(function (res) {
      done();
    }).catch(function (err) {
      done(err)
    });
  });
});

describe('POST /api/process-form', function () {
  var url = '/api/process-form';
  var agent;
  before(function (done) {
    appTest.loginWithBankID(done, function (loginAgent) {
      agent = loginAgent;
    });
  });

  it('should respond with 400 if no nID_Server', function (done) {
    appTest.tests.testPOSTUnathorized(url, agent, {}, done);
  });

});


describe('GET /api/process-form/sign/check', function () {
  var agent;
  before(function (done) {
    appTest.loginWithBankID(function (error, loginAgent) {
      if (error) {
        done(error)
      } else {
        agent = loginAgent;
        done();
      }
    });
  });

  it('should respond with 400 if no fileID', function (done) {
    var signCheck = testRequest.get('/api/process-form/sign/check?nID_Server=1');
    agent.attachCookies(signCheck);
    signCheck.expect(400).then(function (res) {
      assertErrorResult(res);
      done();
    }).catch(function (err) {
      done(err)
    });
  });

  it('should respond with 400 if no sURL', function (done) {
    var signCheck = testRequest.get('/api/process-form/sign/check?fileID=1122233');
    agent.attachCookies(signCheck);
    signCheck.expect(400).then(function (res) {
      assertErrorResult(res);
      done();
    }).catch(function (err) {
      done(err)
    });
  });

  it('should respond with 200 with sign object', function (done) {
    var signCheck = testRequest.get('/api/process-form/sign/check?fileID=1&nID_Server=1');
    agent.attachCookies(signCheck);
    signCheck.expect(200).then(function (res) {
      done();
    }).catch(function (err) {
      done(err)
    });
  });

  it('should respond with 500 with error object', function (done) {
    var signCheck = testRequest.get('/api/process-form/sign/check?fileID=2&nID_Server=1');
    agent.attachCookies(signCheck);
    signCheck.expect(500).then(function (res) {
      assertErrorNestedResult(res);
      done();
    }).catch(function (err) {
      done(err)
    });
  });
});
