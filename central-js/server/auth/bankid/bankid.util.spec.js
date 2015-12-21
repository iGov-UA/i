'use strict';

require('../../app.spec');

var keypair = require('keypair')
  , fs = require('fs')
  , appData = require('../../app.data.spec')
  , bankidUtil = require('./bankid.util')
  , should = require('should')
  , assert = require('assert')
  , config = require('../../config/environment');

describe('decrypt object fields', function () {
  it('using generated pair from settings, should not throw any error', function (done) {
    if (!config.bankid.publicKey) {
      done('public key is not specified');
    } else {
      var initialCustomer = JSON.stringify(appData.customer);

      var publicKey = {
        key: fs.readFileSync(config.bankid.publicKey)
      };

      bankidUtil.encryptData(appData.customer, publicKey);
      console.log('encrypted', JSON.stringify(appData.customer));

      bankidUtil.decryptData(appData.customer);
      console.log('decrypted', JSON.stringify(appData.customer));

      assert.equal(initialCustomer, JSON.stringify(appData.customer), "customer should be the same");
      done();
    }
  });

  it('using auto generated pair, should not throw any error', function (done) {
    var pair = keypair({bits: 2048});

    var publicKey = {
      key: pair.public
    };
    var initialCustomer = JSON.stringify(appData.customer);

    bankidUtil.encryptData(appData.customer, publicKey);
    console.log('encrypted', JSON.stringify(appData.customer));

    bankidUtil.decryptData(appData.customer, {key: pair.private});
    console.log('decrypted', JSON.stringify(appData.customer));

    assert.equal(initialCustomer, JSON.stringify(appData.customer), "customer should be the same");
    done();
  });
});
