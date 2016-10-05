'use strict';

var should = require('should')
  , appTest = require('../../app.spec.js')
  , bankidData = require('./bankid.data.spec.js')
  , bankidUtil = require('./../../../server/auth/bankid/bankid.util.js')
  , config = require('../../../server/config/environment/index');

require('./bankid.nock.js');

describe('bankidController initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUController', function () {
    return require('./../../../server/auth/bankid/bankid.controller.js');
  });
});

describe('authorize with bankid', function () {
  it('should go without error and with non-encrypted customer data', function (done) {
    var agent;
    appTest.loginWithBankID(function (error) {
      if (error) {
        done(new Error(JSON.stringify(error)))
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    }, bankidData.codes.forCustomerDataResponse);
  });

  it('should handle error with non-encrypted customer data', function (done) {
    var agent;
    appTest.loginWithBankID(function (error) {
        done();
      }, function (loginAgent) {
        agent = loginAgent;
      },
      bankidData.codes.forCustomerDataResponseError,
      appTest.AUTH_MODE.SUCCESS_ON_ERROR);
  });
});
