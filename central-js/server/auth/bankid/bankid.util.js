'use strict';

var config = require('../../config/environment')
  , fs = require('fs')
  , crypto = require('crypto')
  , constants = require('constants')
  , url = require('url');

var privateKeyFromConfigs;

var initPrivateKey = function() {
  if (config.bankid.enableCipher === 'true' && config.bankid.privateKey && !privateKeyFromConfigs) {
    try {
      var key = fs.readFileSync(config.bankid.privateKey);
      privateKeyFromConfigs = {
        key: key,
        passphrase: config.bankid.privateKeyPassphrase,
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
    protocol: config.bankid.sProtocol_AccessService_BankID,
    hostname: config.bankid.sHost_AccessService_BankID,
    pathname: pathname ? pathname : ''
  });
};

var getResourceURL = function (pathname) {
  return url.format({
    protocol: config.bankid.sProtocol_ResourceService_BankID,
    hostname: config.bankid.sHost_ResourceService_BankID,
    pathname: pathname ? pathname : ''
  });
};

var baseURls = {
  access: {
    base: getURL(),
    path: {
      token: '/DataAccessService/oauth/token',
      auth: '/DataAccessService/das/authorize'
    }
  },
  resource: {
    base: getResourceURL(),
    path: {
      info: '/ResourceService/checked/data',
      sign: '/ResourceService/checked/uploadFileForSignature',
      claim: '/ResourceService/checked/claim/:codeValue/clientPdfClaim'
    }
  }
};

module.exports.getBaseURLs = function () {
  return baseURls;
};

module.exports.getInfoURL = function () {
  return getResourceURL(baseURls.resource.path.info);
};

module.exports.getUploadFileForSignatureURL = function () {
  return getResourceURL(baseURls.resource.path.sign);
};

module.exports.getClientPdfClaim = function (codeValue) {
  return getResourceURL(baseURls.resource.path.claim.replace(/:codeValue/, codeValue));
};

module.exports.getTokenURL = function () {
  return getURL(baseURls.access.path.token);
};

module.exports.getAuthorizationURL = function () {
  return getURL(baseURls.access.path.auth);
};

module.exports.getAuth = function (accessToken) {
  return 'Bearer ' + accessToken + ', Id ' + config.bankid.client_id;
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

function decrypt(value, key, privateKey) {
  if (isEncrypted(value, key)) {
    try {
      return crypto.privateDecrypt(privateKey, new Buffer(value, 'base64')).toString('utf8');
    } catch (err) {
      throw new Error("can't decrypt value " + value + " field " + key + " because of\n" + err.message);
    }
  } else {
    return value;
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

module.exports.encryptData = function (customerData, publicKey) {
  iterateObj(customerData, function (value, key) {
    return isEncrypted(value, key)
      ? crypto.publicEncrypt(publicKey, new Buffer(value, 'utf-8')).toString('base64')
      : value;
  });
};

module.exports.decryptData = function (customerData, privateKey) {
  iterateObj(customerData, function (value, key) {
    return decrypt(value, key, privateKey ? privateKey : privateKeyFromConfigs)
  });
};

module.exports.initPrivateKey = initPrivateKey;


