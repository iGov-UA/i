'use strict';

var url = require('url');
var config = require('../../config/environment');

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
    base : getResourceURL(),
    path: {
      info : '/ResourceService/checked/data',
      sign: '/ResourceService/checked/uploadFileForSignature',
      claim : '/ResourceService/checked/claim/:codeValue/clientPdfClaim'
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


