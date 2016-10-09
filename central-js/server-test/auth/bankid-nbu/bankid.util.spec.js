'use strict';

var should = require('should');
var appTest = require('../../app.spec.js');

describe('bankidNBUUtil initialization test', function () {
  appTest.runModuleInitializationTest('bankidNBUUtil', function () {
    return require('./../../../server/auth/bankid-nbu/bankid.util.js');
  });
});

describe('decrypt object fields', function () {
  var fs = require('fs')
    , constants = require('constants')
    , appData = require('../../app.data.spec.js')
    , should = require('should')
    , assert = require('assert')
    , config = require('../../../server/config/environment/index')
    , bankidNBUUtil = require('./../../../server/auth/bankid-nbu/bankid.util.js');


  before(function (done) {
    config.bankidnbu.enableCipher = 'true';
    bankidNBUUtil.initPrivateKey();
    done();
  });

  it('using generated pair from settings, should not throw any error', function (done) {
    var initialCustomer = JSON.stringify(appData.customerShort);
    var testCustomer = JSON.parse(JSON.stringify(appData.customerShort));

    var publicKey = {
      key: fs.readFileSync(__dirname + '/../../../iGov_sgn_cert.pem'),
      padding: constants.RSA_PKCS1_PADDING
    };

    var encrypted = bankidNBUUtil.encryptData(testCustomer, publicKey);
    console.log('encrypted ', encrypted);
    var decryptedCustomer = bankidNBUUtil.decryptData(encrypted);
    console.log('decrypted ', JSON.stringify(decryptedCustomer));

    assert.equal(JSON.stringify(decryptedCustomer), initialCustomer, "customer should be the same");
    done();
  });

});
