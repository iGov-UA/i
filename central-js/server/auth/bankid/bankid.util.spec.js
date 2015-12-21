'use strict';

require('../../app.spec');

var crypto = require('crypto')
  , keypair = require('keypair')
  , appData = require('../../app.data.spec')
  , bankidUtil = require('./bankid.util')
  , should = require('should')
  , assert = require('assert');

describe('decrypt object fields', function () {
  it('using auto generated pair, should not throw any error', function (done) {
    var pair = keypair({bits: 2048});
    var publicKey = {
      key: pair.public
    };
    var initialCustomer = JSON.stringify(appData.customer);

    bankidUtil.iterateObj(appData.customer, function (value, key) {
      return ['number', 'type'].indexOf(key) === -1
        ? crypto.publicEncrypt(publicKey, new Buffer(value, 'utf-8')).toString('base64')
        : value;
    });
    bankidUtil.decryptData(appData.customer, {key: pair.private});

    assert.equal(initialCustomer, JSON.stringify(appData.customer), "customer should be the same");
    done();
  });
});
