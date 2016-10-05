'use strict';

var should = require('should');
var appTest = require('../../app.spec.js');

describe('bankidNBUPassport initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUPassport', function () {
    return require('./../../../server/auth/bankid-nbu/bankid.passport.js');
  });
});
