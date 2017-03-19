'use strict';

var should = require('should')
  , appTest = require('../../app.spec')
  , bankidNBUData = require('./bankid.data.spec')
  , bankidNBUUtil = require('./bankid.util')
  , config = require('../../config/environment');

require('./bankid.nock.js');

describe('bankidNBUController initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUController', function () {
    return require('./bankid.controller');
  });
});

describe('authorize with bankid nbu', function () {
  it('should go without error and with non-encrypted customer data', function (done) {
    var agent;
    appTest.loginWithBankIDNBU(function (error) {
      if (error) {
        done(error)
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    }, bankidNBUData.codes.forCustomerDataResponse);
  });
});

describe('authorize with bankid nbu and encrypted customer data', function () {
  var fs = require('fs')
    , constants = require('constants');

  before(function (done) {
    config.bankidnbu.enableCipher = 'true';
    bankidNBUUtil.initPrivateKey();
    done();
  });

  it('should go without error and with encrypted customer data', function (done) {
    var agent;
    appTest.loginWithBankIDNBU(function (error) {
      if (error) {
        done(error)
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    }, bankidNBUData.codes.forCustomerDataCryptoResponse);
  });

  after(function (done) {
    config.bankidnbu.enableCipher = 'false';
    done();
  });
});
