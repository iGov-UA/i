'use strict';

var config = require('../../config/environment')
  , fs = require('fs')
  , crypto = require('crypto')
  , url = require('url');

var privateKeyFromConfigs;

(function initPrivateKey() {
  if (config.bankid.privateKey && !privateKeyFromConfigs) {
    var key = fs.readFileSync(config.bankid.privateKey);
    privateKeyFromConfigs = {
      key: key,
      passphrase: config.bankid.privateKeyPassphrase
    }
  }
})();

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

var noEncryptionFields = ['number', 'type'];

function decrypt(value, key, privateKey) {
  if (noEncryptionFields.indexOf(key) === -1) {
    return crypto.privateDecrypt(privateKey, new Buffer(value, 'base64')).toString('utf8');
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

module.exports.decryptData = function (customerData, privateKey) {
  iterateObj(customerData, function (value, key) {
    return decrypt(value, key, privateKey ? privateKey : privateKeyFromConfigs)
  });
};


