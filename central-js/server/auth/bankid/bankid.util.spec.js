'use strict';

var should = require('should');
var appTest = require('../../app.spec');
var bankidUtil = require('./bankid.util');
var appData = require('../../app.data.spec');

describe('decrypt object fields', function () {
  it('should not throw any error', function (done) {
    var fs = require('fs')
      , ursa = require('ursa')
      , crt
      , key
      , msg
      ;

    crt = ursa.createPublicKey(fs.readFileSync('/home/domash/keys/iGov_sgn_cert.pem'));

    //bankidUtil.iterateObj(appData.customer, function(value){
    //  return crt.encrypt(value, 'utf8', 'base64');
    //});
    //console.log('encrypted ', JSON.stringify(appData.customer), '\n');
    bankidUtil.decryptData(appData.customer);
    //console.log('decrypted', JSON.stringify(appData.customer), '\n');

    done();
  });
});
