'use strict';

var config = require('../../config/environment')
  , fs = require('fs')
  , crypto = require('crypto')
  , constants = require('constants')
  , url = require('url');

var privateKeyFromConfigs;

function isCipherEnabled() {
  return config.bankidnbu.enableCipher === 'true' || config.bankidnbu.enableCipher === true;
}

var initPrivateKey = function () {
  if ((config.bankidnbu.enableCipher === 'true' || config.bankidnbu.enableCipher === true) && config.bankidnbu.privateKey && !privateKeyFromConfigs) {
    try {
      var key = fs.readFileSync(config.bankidnbu.privateKey);
      privateKeyFromConfigs = {
        key: key,
        passphrase: config.bankidnbu.privateKeyPassphrase,
        padding: constants.RSA_PKCS1_PADDING
      }
    } catch (err) {
      throw new Error('Can\'t read private key file for bankID. ' +
        'It should be specified. ' +
        'See config/local.env.sample.js ' +
        'Nested message if\n' + err.message);
    }
  }
};

initPrivateKey();

var getURL = function (pathname) {
  return url.format({
    protocol: config.bankidnbu.sProtocol_AccessService_BankID,
    hostname: config.bankidnbu.sHost_AccessService_BankID,
    pathname: pathname ? pathname : ''
  });
};

var getResourceURL = function (pathname) {
  return url.format({
    protocol: config.bankidnbu.sProtocol_ResourceService_BankID,
    hostname: config.bankidnbu.sHost_ResourceService_BankID,
    pathname: pathname ? pathname : ''
  });
};

var baseURls = {
  access: {
    base: getURL(),
    path: {
      token: '/v1/bank/oauth2/token',
      auth: '/v1/bank/oauth2/authorize'
    }
  },
  resource: {
    base: getResourceURL(),
    path: {
      info: '/v1/bank/resource/client'
    }
  }
};

module.exports.getBaseURLs = function () {
  return baseURls;
};

module.exports.getInfoURL = function () {
  return getResourceURL(baseURls.resource.path.info);
};

module.exports.getTokenURL = function () {
  return getURL(baseURls.access.path.token);
};

module.exports.getAuthorizationURL = function () {
  return getURL(baseURls.access.path.auth);
};

module.exports.getAuth = function (accessToken) {
  return 'Bearer ' + accessToken;
};

module.exports.decryptField = function (value, privateKey) {
  var self = this;
  if (isCipherEnabled()) {
    return decryptValue(value, privateKey ? privateKey : privateKeyFromConfigs);
  } else {
    return value;
  }
};

module.exports.decryptCallback = function (callback) {
  var self = this;

  return function (error, response, body) {
    if (isCipherEnabled() && body && body.customerCrypto) {
      body.customer = self.decryptData(body.customerCrypto);
      delete body.customerCrypto;
    }
    callback(error, response, body);
  }
};

var noEncryptionFields = ['number', 'type', 'signature'];

function isEncrypted(value, key) {
  if (noEncryptionFields.indexOf(key) === -1) {
    return true;
  } else {
    if (key === 'number') {
      if (Number.isNaN(Number.parseInt(value))) {
        return true;
      }
    }
    return false;
  }
}

function iterateObj(obj, call) {
  Object.keys(obj).forEach(function (key) {
    if (typeof obj[key] === 'object') {
      return iterateObj(obj[key], call);
    }
    obj[key] = call(obj[key], key);
  });
}

module.exports.iterateObj = function (obj, call) {
  return iterateObj(obj, call);
};

function decryptValue(value, privateKey) {
  try {
    return crypto.privateDecrypt(privateKey, new Buffer(value, 'base64')).toString('utf8');
  } catch (err) {
    throw new Error("can't decrypt value " + value + " because of\n" + err.message);
  }
}

module.exports.encryptData = function (customerData, publicKey) {
  iterateObj(customerData, function (value, key) {
    return isEncrypted(value, key)
      ? crypto.publicEncrypt(publicKey, new Buffer(value, 'utf-8')).toString('base64')
      : value;
  });
  return customerData;
};

module.exports.decryptData = function (customerData, privateKey) {
  iterateObj(customerData, function (value, key) {
    return decrypt(value, key, privateKey ? privateKey : privateKeyFromConfigs)
  });
};

module.exports.initPrivateKey = initPrivateKey;
