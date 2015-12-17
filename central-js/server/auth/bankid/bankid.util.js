'use strict';

var config = require('../../config/environment')
  , fs = require('fs')
  , ursa = require('ursa')
  , url = require('url')
  , Buffer = require('buffer').Buffer;

var privateKey;

(function initPrivateKey(){
  if(config.bankid.privateKey && !privateKey) {
    privateKey = ursa.createPrivateKey(fs.readFileSync(config.bankid.privateKey));
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

function decrypt(value) {
  return privateKey.decrypt(value, 'base64', 'utf8', ursa.RSA_NO_PADDING);
}

function iterateObj(obj, call) {
  Object.keys(obj).forEach(function (key) {
    if (typeof obj[key] === 'object') {
      return iterateObj(obj[key], call);
    }
    obj[key] = call(obj[key]);
  });
}

module.exports.decryptData = function (customerData) {
  iterateObj(customerData, decrypt);
};


