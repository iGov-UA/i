'use strict';

var should = require('should')
  , appTest = require('../../app.spec')
  , bankidNBUData = require('./bankid.data.spec')
  , testRequest = appTest.testRequest;

require('./bankid.nock.js');

describe('bankidNBUController initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUController', function () {
    return require('./bankid.controller');
  });
});

describe('authorize with bankid nbu', function () {
  it('should go without error and with non-encrypted customer data', function (done) {
    var agent;
    appTest.loginWithBankIDNBU(function (error, loginAgent) {
      if (error) {
        done(error)
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    }, bankidNBUData.codes.forCustomerDataResponse);
  });

  it('should go without error and with encrypted customer data', function (done) {
    var agent;
    appTest.loginWithBankIDNBU(function (error, loginAgent) {
      if (error) {
        done(error)
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    }, bankidNBUData.codes.forCustomerDataCryptoResponse);
  });
});
