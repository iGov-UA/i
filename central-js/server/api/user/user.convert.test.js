'use strict';

var should = require('should');
var userConvert = require('./user.convert');
var subjectServiceTest = require('./../subject/subject.service.test');

suite('user.convert', function () {
  suite('#convertToCanonical()', function () {
    test('should convert from email user to customer', function () {
      var type = 'email';

      var customer = userConvert.convertToCanonical(type, subjectServiceTest.testData.subjectHuman);
      console.log(JSON.stringify(customer));

      customer.should.have.property('type');
      customer.should.have.property('lastName');
      customer.should.have.property('firstName');
      customer.should.have.property('middleName');
      customer.should.have.property('documents');

      var passport = customer.documents[0];
      passport.should.have.property('type');
      passport.should.have.property('number');
      passport.should.have.property('series');
    });
  });
});
