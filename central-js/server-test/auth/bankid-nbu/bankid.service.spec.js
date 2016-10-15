'use strict';

var should = require('should');
var appTest = require('../../app.spec.js');

describe('bankidNBUService initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUService', function () {
    return require('./../../../server/auth/bankid-nbu/bankid.service.js');
  });
});



