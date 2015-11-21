'use strict';

var should = require('should');
var appTest = require('../app.spec');
var agent = appTest.agent;

describe('POST /auth/logout', function () {
  it('should respond with 200', function (done) {
    appTest.authAndTest(function (error) {
      if (error) {
        done(error)
      } else {
        agent
          .post('/auth/logout')
          .expect(200)
          .then(function (res) {
            console.log('result!!!');
          }).catch(function (err) {
            done(err)
          });
      }
    });
  });
});
