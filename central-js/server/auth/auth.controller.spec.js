'use strict';

var should = require('should');
var appTest = require('../app.spec');
var request = appTest.createSuperTestWithDefaultSession();

describe('POST /auth/logout', function () {
  it('should respond with 200', function (done) {
    request
      .post('/auth/logout')
      .send({})
      .expect(200)
      .expect('Content-Type', /json/)
      .end(function (err, res) {
        if (err) return done(err);

        done();
      });
  });
});
