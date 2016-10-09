'use strict';

var appTest = require('../../app.spec.js')
  , testRequest = appTest.testRequest
  , should = require('should');

require('./subject.service.nock.js');


describe('organ.service service tests', function () {
  it('should respond with 200 and without errors', function (done) {
    var signCheck = testRequest.get('/api/subject/organ-join-tax?someParam=1');
    signCheck.expect(200).then(function (res) {
      done();
    }).catch(function (err) {
      done(err)
    });
  });
});
