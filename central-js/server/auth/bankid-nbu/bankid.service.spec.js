'use strict';

var should = require('should');
var appTest = require('../../app.spec');

describe('bankidNBUService initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUService', function () {
    return require('./bankid.service');
  });
});



