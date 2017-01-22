'use strict';

require('../../app.spec');

var keypair = require('keypair')
  , fs = require('fs')
  , constants = require('constants')
  , appData = require('../../app.data.spec')
  , bankidUtil = require('./bankid.util')
  , should = require('should')
  , assert = require('assert')
  , config = require('../../config/environment');

describe('decrypt object fields', function () {
  before(function (done) {
    config.bankid.enableCipher = 'true';
    bankidUtil.initPrivateKey();
    done();
  });

  it('using generated pair from settings, should not throw any error', function (done) {
    var initialCustomer = JSON.stringify(appData.customer);
    var testCustomer = JSON.parse(JSON.stringify(appData.customer));

    var publicKey = {
      key: fs.readFileSync(__dirname + '/../../../iGov_sgn_cert.pem'),
      padding: constants.RSA_PKCS1_PADDING
    };

    bankidUtil.encryptData(testCustomer, publicKey);
    console.log('encrypted ', JSON.stringify(testCustomer));
    bankidUtil.decryptData(testCustomer);
    console.log('decrypted ', JSON.stringify(testCustomer));

    assert.equal(JSON.stringify(testCustomer), initialCustomer, "customer should be the same");
    done();
  });

  it('using auto generated pair, should not throw any error', function (done) {
    var pair = keypair({bits: 2048});

    var publicKey = {
      key: pair.public
    };
    var initialCustomer = JSON.stringify(appData.customer);
    var testCustomer = JSON.parse(JSON.stringify(appData.customer));

    bankidUtil.encryptData(testCustomer, publicKey);
    bankidUtil.decryptData(testCustomer, {key: pair.private});

    assert.equal(JSON.stringify(testCustomer), initialCustomer, "customer should be the same");
    done();
  });
});
