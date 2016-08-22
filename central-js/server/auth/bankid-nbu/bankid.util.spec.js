'use strict';

var should = require('should');
var appTest = require('../../app.spec');

describe('bankidNBUUtil initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUUtil', function () {
    return require('./bankid.util');
  });
});
