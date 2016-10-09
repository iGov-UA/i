'use strict';

var should = require('should');

var testRequest;

function assertErrorResult(res) {
  res.should.have.property('body');
  res.body.should.have.property('code');
  res.body.should.have.property('message');
}

function assertErrorNestedResult(res) {
  res.should.have.property('body');
  res.body.should.have.property('code');
  res.body.should.have.property('message');
  res.body.should.have.property('nested');
}

function defaultEnd(done) {
  return function (err, res) {
    if (err) {
      return done(err);
    }
    done();
  }
}

function testPOST(urlWithParams, agent) {
  var testPOSTRequest = testRequest.post(urlWithParams);
  if (agent) {
    agent.attachCookies(testPOSTRequest);
  }
  return testPOSTRequest;
}

function testGET(urlWithParams, agent) {
  var testGETRequest = testRequest.get(urlWithParams);
  if (agent) {
    agent.attachCookies(testGETRequest);
  }
  return testGETRequest;
}

function testGETInputParamsAbsence(urlWithParams, agent, done) {
  testGET(urlWithParams, agent).expect(400).then(function (res) {
    assertErrorResult(res);
    done();
  }).catch(function (err) {
    done(err)
  });
}

function testGETUnathorized(urlWithParams, done) {
  testGET(urlWithParams).expect(401).then(function (res) {
    done();
  }).catch(function (err) {
    done(err)
  });
}

function testPOSTUnathorized(urlWithParams, agent, body, done) {
  var postForm = testPOST(urlWithParams, agent);
  postForm.send(body)
    .expect(400)
    .end(defaultEnd(done));
}

function assertNoError(done) {
  return function (error) {
    if(error){
      done(error);
    } else {
      done();
    }
  }
}

module.exports = function (testRequestParam) {
  testRequest = testRequestParam;

  return {
    assertNoError : assertNoError,
    assertErrorResult: assertErrorResult,
    assertErrorNestedResult: assertErrorNestedResult,
    testGET: testGET,
    testPOST: testPOST,
    testGETInputParamsAbsence: testGETInputParamsAbsence,
    testGETUnathorized: testGETUnathorized,
    testPOSTUnathorized: testPOSTUnathorized
  }
};
