'use strict';

var path = require('path')
  , fs = require('fs')
  , _ = require('lodash');

function getCustomConfig() {
  try {
    return require(__dirname + '/../../..' + '/process-custom.json').env
  } catch (e) {
    console.log('Can\'t load process-custom.json. ' + e);
    return null;
  }
}

function getConfig() {
  try {
    return require(__dirname + '/../../..' + '/process.json').env
  } catch (e) {
    console.log('Can\'t load process.json. ' + e);
    return null;
  }
}

function convertStringsToObject(processProps) {
  processProps.BackProxy_Central =
    typeof processProps.BackProxy_Central == 'string' ?
      JSON.parse(processProps.BackProxy_Central) :
      processProps.BackProxy_Central;

  processProps.BackProxySession_Central =
    typeof processProps.BackProxySession_Central == 'string' ?
      JSON.parse(processProps.BackProxySession_Central) :
      processProps.BackProxySession_Central;

  processProps.Back_Central =
    typeof processProps.Back_Central == 'string' ?
      JSON.parse(processProps.Back_Central) :
      processProps.Back_Central;

  processProps.Auth_BankID =
    typeof processProps.Auth_BankID == 'string' ?
      JSON.parse(processProps.Auth_BankID) :
      processProps.Auth_BankID;

  processProps.Auth_BankID_NBU =
    typeof processProps.Auth_BankID_NBU == 'string' ?
      JSON.parse(processProps.Auth_BankID_NBU) :
      processProps.Auth_BankID_NBU;

  processProps.Auth_CardKyanyn =
    typeof processProps.Auth_CardKyanyn == 'string' ?
      JSON.parse(processProps.Auth_CardKyanyn) :
      processProps.Auth_CardKyanyn;
}

var processProps;
var defaultConfig = getConfig();
var customConfig = getCustomConfig();

if (defaultConfig && customConfig) {
  //processProps = customConfig;
  processProps = _.merge(defaultConfig, customConfig);
} else if (!defaultConfig && customConfig) {
  processProps = customConfig;
} else if (defaultConfig && !customConfig) {
  processProps = defaultConfig;
} else if (!defaultConfig && !customConfig) {
  processProps = _.merge({}, process.env);
  convertStringsToObject(processProps);
}

function parsePath(pathStr) {
  return pathStr.split('/');
}

//If path is absolute? return as it is, else return relative path
var sPrivateKeyPathAuthBankID;
var pathStr;
var pathArr;
try {

  pathStr = processProps.Auth_BankID ? processProps.Auth_BankID.sPrivateKeyPath_Auth_BankID : process.env.BANKID_PRIVATE_KEY;
  pathArr = parsePath(processProps.Auth_BankID ? processProps.Auth_BankID.sPrivateKeyPath_Auth_BankID : process.env.BANKID_PRIVATE_KEY);

  if (pathArr.length > 1) {
    sPrivateKeyPathAuthBankID = pathStr;
  } else {
    sPrivateKeyPathAuthBankID = (__dirname + '/../../../' + pathStr);
  }

} catch (e) {
  sPrivateKeyPathAuthBankID = '';
}

var sPrivateKeyPathAuthBankIDNBU;

try {

  pathStr = processProps.Auth_BankID_NBU ? processProps.Auth_BankID_NBU.sPrivateKeyPath_Auth_BankID : process.env.BANKIDNBU_PRIVATE_KEY;
  pathArr = parsePath(processProps.Auth_BankID_NBU ? processProps.Auth_BankID_NBU.sPrivateKeyPath_Auth_BankID : process.env.BANKIDNBU_PRIVATE_KEY);

  if (pathArr.length > 1) {
    sPrivateKeyPathAuthBankIDNBU = pathStr;
  } else {
    sPrivateKeyPathAuthBankIDNBU = (__dirname + '/../../../' + pathStr);
  }

} catch (e) {
  sPrivateKeyPathAuthBankIDNBU = '';
}

function parseUrl(url) {
  var pattern = "^(([^:/\\?#]+):)?(//(([^:/\\?#]*)(?::([^/\\?#]*))?))?([^\\?#]*)(\\?([^#]*))?(#(.*))?$",
    regex = new RegExp(pattern),
    parts = regex.exec(url);

  return {
    protocol: parts[2],
    host: parts[5],
    port: parts[6],
    suffix: parts[7]
  };
}

//Split URL string to protocol, host, port
var sURLBackProxyCentralParts,
  sURLBackCentralParts,
  sURLAccessAuthBankIDParts,
  sURLResourceAuthBankIDParts,
  sURLAccessAuthBankIDNBUParts,
  sURLResourceAuthBankIDNBUParts,
  sURLAuthCardKyanynParts;
try {
  sURLBackProxyCentralParts = parseUrl(processProps.BackProxy_Central.sURL_BackProxy_Central);
  sURLBackCentralParts = parseUrl(processProps.Back_Central.sURL_Back_Central);
  sURLAccessAuthBankIDParts = parseUrl(processProps.Auth_BankID.sURL_Access_Auth_BankID);
  sURLResourceAuthBankIDParts = parseUrl(processProps.Auth_BankID.sURL_Resource_Auth_BankID);
  sURLAccessAuthBankIDNBUParts = parseUrl(processProps.Auth_BankID_NBU.sURL_Access_Auth_BankID);
  sURLResourceAuthBankIDNBUParts = parseUrl(processProps.Auth_BankID_NBU.sURL_Resource_Auth_BankID);
  sURLAuthCardKyanynParts = parseUrl(processProps.Auth_CardKyanyn.sURL_Auth_CardKyanyn);
} catch (e) {
  sURLBackProxyCentralParts = undefined;
  sURLBackCentralParts = undefined;
  sURLAccessAuthBankIDParts = undefined;
  sURLResourceAuthBankIDParts = undefined;
  sURLAuthCardKyanynParts = undefined;
}

// All configurations will extend these options
// ============================================
var all = {
  env: processProps.sProfile_Application || process.env.NODE_ENV,

  // Root path of server
  root: path.normalize(__dirname + '/../../..'),

  bCompile: (processProps.bCompile_Application === "TRUE"),

  bTest: (processProps.bTest === "TRUE"),

  debug: (processProps.bDebug_Application === "TRUE"),


  server: {
    //sServerRegion: processProps.BackProxy_Central ? processProps.BackProxy_Central.sURL_BackProxy_Central : process.env.sServerRegion,
    sServerRegion: sURLBackProxyCentralParts ? (sURLBackProxyCentralParts.protocol + '://' + sURLBackProxyCentralParts.host) : process.env.sServerRegion,
    //sServerRegion: processProps.sServerRegion || process.env.sServerRegion,
    protocol: sURLBackProxyCentralParts ? sURLBackProxyCentralParts.protocol : process.env.SERVER_PROTOCOL,
    port: sURLBackProxyCentralParts ? sURLBackProxyCentralParts.port : process.env.SERVER_PORT,
    key: processProps.BackProxy_Central ? processProps.BackProxy_Central.sKeyPath_BackProxy_Central : process.env.SERVER_KEY,
    cert: processProps.BackProxy_Central ? processProps.BackProxy_Central.sCertPath_BackProxy_Central : process.env.SERVER_CERT,

    session: {
      secret: processProps.BackProxySession_Central ? processProps.BackProxySession_Central.sSecret_BackProxySession_Central : process.env.SESSION_SECRET,
      key: ((processProps.BackProxySession_Central ? processProps.BackProxySession_Central.sKey1_BackProxySession_Central : process.env.SESSION_KEY_ONE) &&
      (processProps.BackProxySession_Central ? processProps.BackProxySession_Central.sKey2_BackProxySession_Central : process.env.SESSION_KEY_TWO) ?
        [(processProps.BackProxySession_Central ? processProps.BackProxySession_Central.sKey1_BackProxySession_Central : process.env.SESSION_KEY_ONE),
          (processProps.BackProxySession_Central ? processProps.BackProxySession_Central.sKey2_BackProxySession_Central : process.env.SESSION_KEY_TWO)] :
        undefined),
      secure: processProps.BackProxySession_Central ? (processProps.BackProxySession_Central.bSecure_BackProxySession_Central === "TRUE") : process.env.SESSION_SECURE,
      maxAge: processProps.BackProxySession_Central ? processProps.BackProxySession_Central.nLiveMS_BackProxySession_Central : process.env.SESSION_MAX_AGE  // 3 * 60 * 1000 = 3 min
    }
  },

  activiti: {
    protocol: sURLBackCentralParts ? sURLBackCentralParts.protocol : process.env.ACTIVITI_PROTOCOL,
    hostname: sURLBackCentralParts ? sURLBackCentralParts.host : process.env.ACTIVITI_HOSTNAME,
    port: sURLBackCentralParts ? sURLBackCentralParts.port : process.env.ACTIVITI_PORT,
    path: sURLBackCentralParts ? sURLBackCentralParts.suffix : process.env.ACTIVITI_PATH,
    username: processProps.Back_Central ? processProps.Back_Central.sLogin_Back_Central : process.env.ACTIVITI_USER,
    password: processProps.Back_Central ? processProps.Back_Central.sPassword_Back_Central : process.env.ACTIVITI_PASSWORD
  },

  bankid: {
    sProtocol_AccessService_BankID: sURLAccessAuthBankIDParts ? sURLAccessAuthBankIDParts.protocol : process.env.BANKID_SPROTOCOL_ACCESS_SERVICE,
    sHost_AccessService_BankID: sURLAccessAuthBankIDParts ? sURLAccessAuthBankIDParts.host : process.env.BANKID_SHOST_ACCESS_SERVICE,
    sProtocol_ResourceService_BankID: sURLResourceAuthBankIDParts ? sURLResourceAuthBankIDParts.protocol : process.env.BANKID_SPROTOCOL_RESOURC_SERVICE,
    sHost_ResourceService_BankID: sURLResourceAuthBankIDParts ? sURLResourceAuthBankIDParts.host : process.env.BANKID_SHOST_RESOURCE_SERVICE,
    client_id: processProps.Auth_BankID ? processProps.Auth_BankID.sClientID_Auth_BankID : process.env.BANKID_CLIENTID,
    client_secret: processProps.Auth_BankID ? processProps.Auth_BankID.sClientSecret_Auth_BankID : process.env.BANKID_CLIENT_SECRET,
    /**
     * Should be used only in connection to privateKey and privateKeyPassphrase,
     * when BANKID enables ciphering of its data. In that case BANKID service has
     * public key on its side, generated from privateKey in config
     * */
    enableCipher: processProps.Auth_BankID ? (processProps.Auth_BankID.bCrypt_Auth_BankID === "TRUE") : process.env.BANKID_ENABLE_CIPHER,

    /**
     * Will work and Should be specified if enableCipher === true
     */
    privateKey: sPrivateKeyPathAuthBankID,

    /**
     * It's passphrase for privateKey.
     * Will work and Should be specified if enableCipher === true.
     */
    privateKeyPassphrase: processProps.Auth_BankID ? processProps.Auth_BankID.sPrivateKeyPassphrase_Auth_BankID : process.env.BANKID_PRIVATE_KEY_PASSPHRASE

  },

  bankidnbu: {
    sProtocol_AccessService_BankID: sURLAccessAuthBankIDNBUParts ? sURLAccessAuthBankIDNBUParts.protocol : process.env.BANKIDNBU_SPROTOCOL_ACCESS_SERVICE,
    sHost_AccessService_BankID: sURLAccessAuthBankIDNBUParts ? sURLAccessAuthBankIDNBUParts.host : process.env.BANKIDNBU_SHOST_ACCESS_SERVICE,
    sProtocol_ResourceService_BankID: sURLResourceAuthBankIDNBUParts ? sURLResourceAuthBankIDNBUParts.protocol : process.env.BANKIDNBU_SPROTOCOL_RESOURC_SERVICE,
    sHost_ResourceService_BankID: sURLResourceAuthBankIDNBUParts ? sURLResourceAuthBankIDNBUParts.host : process.env.BANKIDNBU_SHOST_RESOURCE_SERVICE,
    client_id: processProps.Auth_BankID_NBU ? processProps.Auth_BankID_NBU.sClientID_Auth_BankID : process.env.BANKIDNBU_CLIENTID,
    client_secret: processProps.Auth_BankID_NBU ? processProps.Auth_BankID_NBU.sClientSecret_Auth_BankID : process.env.BANKIDNBU_CLIENT_SECRET,
    enableCipher: processProps.Auth_BankID_NBU ? (processProps.Auth_BankID_NBU.bCrypt_Auth_BankID === "TRUE") : process.env.BANKIDNBU_ENABLE_CIPHER,
    privateKey: sPrivateKeyPathAuthBankIDNBU,
    privateKeyPassphrase: processProps.Auth_BankID_NBU ? processProps.Auth_BankID_NBU.sPrivateKeyPassphrase_Auth_BankID : process.env.BANKIDNBU_PRIVATE_KEY_PASSPHRASE
  },

  soccard: {
    socCardAPIProtocol: sURLAuthCardKyanynParts ? sURLAuthCardKyanynParts.protocol : process.env.KC_SPROTOCOL_ACCESS_SERVICE,
    socCardAPIHostname: sURLAuthCardKyanynParts ? sURLAuthCardKyanynParts.host : process.env.KC_SHOST_ACCESS_SERVICE,
    socCardAPIVersion: processProps.Auth_CardKyanyn ? processProps.Auth_CardKyanyn.sVersion_Auth_CardKyanyn : process.env.SOC_CARD_APIVERSION,
    socCardAPIClientID: processProps.Auth_CardKyanyn ? processProps.Auth_CardKyanyn.sClientID_Auth_CardKyanyn : process.env.SOC_CARD_API_CLIENTID,
    socCardAPIClientSecret: processProps.Auth_CardKyanyn ? processProps.Auth_CardKyanyn.sClientSecret_Auth_CardKyanyn : process.env.SOC_CARD_API_CLIENT_SECRET,
    socCardAPIPrivateKey: processProps.Auth_CardKyanyn ? processProps.Auth_CardKyanyn.sPrivateKeyPath_Auth_CardKyanyn : process.env.SOC_CARD_PRIVATE_KEY,
    socCardAPIPrivateKeyPassphrase: processProps.Auth_CardKyanyn ? processProps.Auth_CardKyanyn.sPrivateKeyPassphrase_Auth_CardKyanyn : process.env.SOC_CARD_PRIVATE_KEY_PASSPHRASE
  },

  hasSoccardAuth: function () {
    return this.soccard.socCardAPIProtocol
      && this.soccard.socCardAPIHostname
      && this.soccard.socCardAPIVersion
      && this.soccard.socCardAPIClientID
      && this.soccard.socCardAPIClientSecret
      && this.soccard.socCardAPIPrivateKey
      && this.soccard.socCardAPIPrivateKeyPassphrase;
  }
};


// Export the config object based on the NODE_ENV
// ==============================================
module.exports = all;

