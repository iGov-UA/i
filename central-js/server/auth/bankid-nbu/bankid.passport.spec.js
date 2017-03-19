'use strict';

var should = require('should');
var appTest = require('../../app.spec');

describe('bankidNBUPassport initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUPassport', function () {
    return require('./bankid.passport');
  });
});
