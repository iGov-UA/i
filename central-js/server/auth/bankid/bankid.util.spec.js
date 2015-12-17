'use strict';

var should = require('should');
var appTest = require('../../app.spec');
var bankidUtil = require('./bankid.util');
var appData = require('../../app.data.spec');

describe('decrypt object fields', function () {
  it('should not throw any error', function (done) {
    bankidUtil.decryptData(appData.encryptedCustomer);
    console.log(JSON.stringify(appData.encryptedCustomer));
    done();
  });
});
