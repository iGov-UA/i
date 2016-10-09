'use strict';

var should = require('should');
var appTest = require('../app.spec.js');

describe('test eds callback', function () {
  it('should create cookie session', function (done) {
    appTest.loginWithEds(done);
  });
});
