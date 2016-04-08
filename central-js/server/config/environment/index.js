'use strict';

var path = require('path');
var _ = require('lodash');

var processProps;
try {

  processProps = require(__dirname + '/../../..'+'/process-custom_.json').env;

} catch (e) {
  processProps = _.merge({}, process.env);

  processProps.BackProxy_Central =
    typeof processProps.BackProxy_Central == 'string' ?
      JSON.parse(processProps.BackProxy_Central) :
      processProps.BackProxy_Central;

  processProps.BackProxySession_Central =
    typeof processProps.BackProxySession_Central == 'string' ?
      JSON.parse(processProps.BackProxySession_Central):
      processProps.BackProxySession_Central;

  processProps.Back_Central =
    typeof processProps.Back_Central == 'string' ?
    JSON.parse(processProps.Back_Central):
    processProps.Back_Central;

  processProps.Auth_BankID =
    typeof processProps.Auth_BankID == 'string' ?
    JSON.parse(processProps.Auth_BankID):
    processProps.Auth_BankID;

  processProps.Auth_CardKyanyn =
    typeof processProps.Auth_CardKyanyn  == 'string' ?
    JSON.parse(processProps.Auth_CardKyanyn):
    processProps.Auth_CardKyanyn;
}


function parsePath(pathStr){
  return pathStr.split('/');
}
//Get path of *.pem files
//If path is absolute? return as it is, else return relative path
var sPrivateKeyPathAuthBankID;
try{

  var pathStr = process.env.BANKID_PRIVATE_KEY || processProps.Auth_BankID.sPrivateKeyPath_Auth_BankID;
  var pathArr = parsePath(process.env.BANKID_PRIVATE_KEY || processProps.Auth_BankID.sPrivateKeyPath_Auth_BankID);

  if( pathArr.length > 1){
    sPrivateKeyPathAuthBankID = pathStr;
  }else{
    sPrivateKeyPathAuthBankID = (__dirname + '/../../..' + pathStr);
  }

}catch(e){
  sPrivateKeyPathAuthBankID = '';
}

function parseUrl(url){
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
    sURLAuthCardKyanynParts;
try{
    sURLBackProxyCentralParts = parseUrl(processProps.BackProxy_Central.sURL_BackProxy_Central),
    sURLBackCentralParts = parseUrl(processProps.Back_Central.sURL_Back_Central),
    sURLAccessAuthBankIDParts = parseUrl(processProps.Auth_BankID.sURL_Access_Auth_BankID),
    sURLResourceAuthBankIDParts = parseUrl(processProps.Auth_BankID.sURL_Resource_Auth_BankID),
    sURLAuthCardKyanynParts = parseUrl(processProps.Auth_CardKyanyn.sURL_Auth_CardKyanyn);
}catch(e){
  sURLBackProxyCentralParts = {};
  sURLBackCentralParts = {};
  sURLAccessAuthBankIDParts = {};
  sURLResourceAuthBankIDParts = {};
  sURLAuthCardKyanynParts = {};
}

// All configurations will extend these options
// ============================================
var all = {
  env: process.env.NODE_ENV || processProps.sProfile_Application,

  // Root path of server
  root: path.normalize(__dirname + '/../../..'),

  bCompile: (processProps.bCompile_Application === "TRUE"),

  debug: (processProps.bDebug_Application === "TRUE"),


  server: {
    sServerRegion: process.env.sServerRegion || processProps.BackProxy_Central.sURL_BackProxy_Central,
    protocol: process.env.SERVER_PROTOCOL || sURLBackProxyCentralParts.protocol,
    port: process.env.SERVER_PORT || sURLBackProxyCentralParts.port,
    key: process.env.SERVER_KEY || processProps.BackProxy_Central.sKeyPath_BackProxy_Central,
    cert: process.env.SERVER_CERT || processProps.BackProxy_Central.sCertPath_BackProxy_Central,

    session: {
      secret: process.env.SESSION_SECRET || processProps.BackProxySession_Central.sSecret_BackProxySession_Central,
      key: ((process.env.SESSION_KEY_ONE || processProps.BackProxySession_Central.sKey1_BackProxySession_Central) &&
            (process.env.SESSION_KEY_TWO || processProps.BackProxySession_Central.sKey2_BackProxySession_Central) ?
            [(process.env.SESSION_KEY_ONE || processProps.BackProxySession_Central.sKey1_BackProxySession_Central),
              (process.env.SESSION_KEY_TWO || processProps.BackProxySession_Central.sKey2_BackProxySession_Central)] :
            undefined),
      secure: process.env.SESSION_SECURE || (processProps.BackProxySession_Central.bSecure_BackProxySession_Central === "TRUE"),
      maxAge: process.env.SESSION_MAX_AGE || processProps.BackProxySession_Central.nLiveMS_BackProxySession_Central // 3 * 60 * 1000 = 3 min
    }
  },

  activiti: {
    protocol: process.env.ACTIVITI_PROTOCOL || sURLBackCentralParts.protocol,
    hostname: process.env.ACTIVITI_HOSTNAME || sURLBackCentralParts.host,
    port: process.env.ACTIVITI_PORT || sURLBackCentralParts.port,
    path: process.env.ACTIVITI_PATH || sURLBackCentralParts.suffix,
    username: process.env.ACTIVITI_USER || processProps.Back_Central.sLogin_Back_Central,
    password: process.env.ACTIVITI_PASSWORD || processProps.Back_Central.sPassword_Back_Central
  },

  bankid: {
    sProtocol_AccessService_BankID: process.env.BANKID_SPROTOCOL_ACCESS_SERVICE || sURLAccessAuthBankIDParts.protocol,
    sHost_AccessService_BankID: process.env.BANKID_SHOST_ACCESS_SERVICE || sURLAccessAuthBankIDParts.host,
    sProtocol_ResourceService_BankID: process.env.BANKID_SPROTOCOL_RESOURC_SERVICE || sURLResourceAuthBankIDParts.protocol,
    sHost_ResourceService_BankID: process.env.BANKID_SHOST_RESOURCE_SERVICE || sURLResourceAuthBankIDParts.host,
    client_id: process.env.BANKID_CLIENTID || processProps.Auth_BankID.sClientID_Auth_BankID,
    client_secret: process.env.BANKID_CLIENT_SECRET || processProps.Auth_BankID.sClientSecret_Auth_BankID,
    /**
     * Should be used only in connection to privateKey and privateKeyPassphrase,
     * when BANKID enables ciphering of its data. In that case BANKID service has
     * public key on its side, generated from privateKey in config
     * */
    enableCipher: process.env.BANKID_ENABLE_CIPHER || (processProps.Auth_BankID.bCrypt_Auth_BankID === "TRUE"),

    /**
     * Will work and Should be specified if enableCipher === true
     */
    privateKey: sPrivateKeyPathAuthBankID,

    /**
     * It's passphrase for privateKey.
     * Will work and Should be specified if enableCipher === true.
     */
    privateKeyPassphrase: process.env.BANKID_PRIVATE_KEY_PASSPHRASE || processProps.Auth_BankID.sPrivateKeyPassphrase_Auth_BankID

  },

  soccard: {
    socCardAPIProtocol: process.env.KC_SPROTOCOL_ACCESS_SERVICE || sURLAuthCardKyanynParts.protocol,
    socCardAPIHostname: process.env.KC_SHOST_ACCESS_SERVICE || sURLAuthCardKyanynParts.host,
    socCardAPIVersion: process.env.SOC_CARD_APIVERSION || processProps.Auth_CardKyanyn.sVersion_Auth_CardKyanyn,
    socCardAPIClientID: process.env.SOC_CARD_API_CLIENTID || processProps.Auth_CardKyanyn.sClientID_Auth_CardKyanyn,
    socCardAPIClientSecret: process.env.SOC_CARD_API_CLIENT_SECRET || processProps.Auth_CardKyanyn.sClientSecret_Auth_CardKyanyn,
    socCardAPIPrivateKey: process.env.SOC_CARD_PRIVATE_KEY || processProps.Auth_CardKyanyn.sPrivateKeyPath_Auth_CardKyanyn,
    socCardAPIPrivateKeyPassphrase: process.env.SOC_CARD_PRIVATE_KEY_PASSPHRASE || processProps.Auth_CardKyanyn.sPrivateKeyPassphrase_Auth_CardKyanyn
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

