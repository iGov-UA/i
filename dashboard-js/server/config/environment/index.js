'use strict';

var path = require('path');
var _ = require('lodash');

var configuration = function () {

  var getCustomConfig = function () {
    try {
      return require(__dirname + '/../../..' + '/process-custom.json').env
    } catch (e) {
      console.log('Can\'t load process-custom.json. ' + e);
      return null;
    }
  };

  var getDefaultConfig = function () {
    try {
      return require(__dirname + '/../../..' + '/process.json').env
    } catch (e) {
      console.log('Can\'t load process.json. ' + e);
      return null;
    }
  };

  var parseUrl = function (url) {
    var pattern = "^(([^:/\\?#]+):)?(//(([^:/\\?#]*)(?::([^/\\?#]*))?))?([^\\?#]*)(\\?([^#]*))?(#(.*))?$";

    try {
      var regex = new RegExp(pattern),
        parts = regex.exec(url);

      return {
        protocol: parts[2],
        host: parts[5],
        port: parts[6],
        suffix: parts[7].replace(/^\/|\/$/g, '')
      };
    } catch (e) {
      return undefined;
    }
  };

  var parsePath = function (pathStr) {
    return pathStr.split('/');
  };

  var processConfigs = {
    customConfig: getCustomConfig(),
    defaultConfig: getDefaultConfig()
  };

  var getFullConfigAsObject = function () {
    var customConfig = processConfigs.customConfig;
    var defaultConfig = processConfigs.defaultConfig;

    if ((defaultConfig && customConfig) || (!defaultConfig && customConfig)) {
      return customConfig;
    } else if (defaultConfig && !customConfig) {
      return defaultConfig;
    } else if (!defaultConfig && !customConfig) {
      var processProps = _.merge({}, process.env);
      return {
        BackProxy_Central: typeof processProps.BackProxy_Central == 'string' ?
          JSON.parse(processProps.BackProxy_Central) :
          processProps.BackProxy_Central,

        BackProxySession_Central: typeof processProps.BackProxySession_Central == 'string' ?
          JSON.parse(processProps.BackProxySession_Central) :
          processProps.BackProxySession_Central,

        Back_Central: typeof processProps.Back_Central == 'string' ?
          JSON.parse(processProps.Back_Central) :
          processProps.Back_Central,

        Auth_BankID: typeof processProps.Auth_BankID == 'string' ?
          JSON.parse(processProps.Auth_BankID) :
          processProps.Auth_BankID,

        Auth_CardKyanyn: typeof processProps.Auth_CardKyanyn == 'string' ?
          JSON.parse(processProps.Auth_CardKyanyn) :
          processProps.Auth_CardKyanyn
      };
    }
  };

  //var getPrivateKeyPath = function () {
  //  try {
  //    var properties = getFullConfigAsObject(),
  //      pathStr = properties.BackProxy_Region ? properties.BackProxy_Region.sPrivateKeyPath_Auth_BankID : process.env.BANKID_PRIVATE_KEY,
  //      pathArr = parsePath(properties.Auth_BankID ? properties.Auth_BankID.sPrivateKeyPath_Auth_BankID : process.env.BANKID_PRIVATE_KEY);
  //
  //    if (pathArr.length > 1) {
  //      return {sPrivateKeyPathAuthBankID: pathStr};
  //    } else {
  //      return {sPrivateKeyPathAuthBankID: (__dirname + '/../../../' + pathStr)};
  //    }
  //
  //  } catch (e) {
  //    return {sPrivateKeyPathAuthBankID: ''};
  //  }
  //};

  var getSplittedURLs = function () {
    var splittedURL = {};

    try {
      var properties = getFullConfigAsObject();
      splittedURL.sURLBackProxyRegionParts = properties.BackProxy_Region ? parseUrl(properties.BackProxy_Region.sURL_BackProxy_Region) : undefined;
      splittedURL.sURLBackRegionParts = properties.Back_Region ? parseUrl(properties.Back_Region.sURL_Back_Region) : undefined;
      splittedURL.sURLBackProxyCentralParts = properties.BackProxy_Central ? parseUrl(properties.BackProxy_Central.sURL_BackProxy_Central) : undefined;
      splittedURL.sURLBackCentralParts = properties.Back_Central ? parseUrl(properties.Back_Central.sURL_Back_Central) : undefined;
    } catch (e) {
      splittedURL.sURLBackProxyRegionParts = undefined;
      splittedURL.sURLBackRegionParts = undefined;
      splittedURL.sURLBackProxyCentralParts = undefined;
      splittedURL.sURLBackCentralParts = undefined;
    }

    return splittedURL;
  };


  return {
    getProperty: function (args) {
      var splittedURLs = getSplittedURLs(),
        //privateKeyPath = getPrivateKeyPath(),
        fullConfig = getFullConfigAsObject(),
        root = _.merge(splittedURLs, fullConfig, {}),
        confArray = [].concat('root',args.split('.'));

      var getVal = function (argArr, argIndex, argObj){
        var length = argArr.length;

        if(argIndex < length - 1){
          argObj = getVal(argArr, (argIndex + 1), argObj[argArr[argIndex + 1]]);
        }
        return argObj;
      };

      try {
        return getVal(confArray, 0, root);
      } catch (e) {
        return undefined;
      }
    }
  };
};

var config = configuration();

// All configurations will extend these options
// ============================================
var all = {
  env: config.getProperty('sProfile_Application') || process.env.NODE_ENV,

  // Root path of server
  root: path.normalize(__dirname + '/../../..'),

  bCompile: (config.getProperty('bCompile_Application') === "TRUE"),

  bTest: (config.getProperty('bTest') === "TRUE"),

  //debug: (config.getProperty('bDebug_Application') === "TRUE"),


  // Server port
  port: config.getProperty('sURLBackProxyRegionParts.port') || process.env.PORT,

  // Secret for session, you will want to change this and make it an environment variable
  secrets: {
    session: config.getProperty('BackProxySession_Region.sSecret_BackProxySession_Region') || process.env.SESSION_SECRET
  },

  activiti: {
    prot: config.getProperty('sURLBackRegionParts.protocol') || process.env.ACTIVITI_PROT,
    host: config.getProperty('sURLBackRegionParts.host') || process.env.ACTIVITI_HOST,
    port: config.getProperty('sURLBackRegionParts.port') || process.env.ACTIVITI_PORT,
    rest: config.getProperty('sURLBackRegionParts.suffix') || process.env.ACTIVITI_REST,
    username: config.getProperty('Back_Region.sLogin_Back_Region') || process.env.ACTIVITI_USER,
    password: config.getProperty('Back_Region.sPassword_Back_Region') || process.env.ACTIVITI_PASSWORD,
    nID_Server: config.getProperty('Back_Region.nID_Server_Back_Region'),
    session: {
      //sessionIdle: process.env.ACTIVITI_SESSION_IDLE || 60 * 80, //sec show warning
      sessionIdle: config.getProperty('BackSession_Region.nIdleMS_BackSession_Region') || 60 * 80, //sec show warning
      //timeOut: process.env.ACTIVITI_SESSION_TIMEOUT || 60 * 20, //sec close session after warning
      timeOut: config.getProperty('BackSession_Region.nTimeoutMS_BackSession_Region') || 60 * 20, //sec close session after warning
      //interval: process.env.ACTIVITI_SESSION_INTERVAL || 60 * 10 //sec update session
      interval: config.getProperty('BackSession_Region.nIntervalMS_BackSession_Region') || 60 * 10 //sec update session
    }
  },
  activiti_central: {
    prot: config.getProperty('sURLBackCentralParts.protocol') || process.env.ACTIVITI_PROT,
    host: config.getProperty('sURLBackCentralParts.host') || process.env.ACTIVITI_HOST,
    port: config.getProperty('sURLBackCentralParts.port') || process.env.ACTIVITI_PORT,
    rest: config.getProperty('sURLBackCentralParts.suffix') || process.env.ACTIVITI_REST,
    username: config.getProperty('Back_Central.sLogin_Back_Central') || process.env.ACTIVITI_USER,
    password: config.getProperty('Back_Central.sPassword_Back_Central') || process.env.ACTIVITI_PASSWORD
  },
  ssl: {
    private_key: config.getProperty('BackProxy_Region.sKeyPath_BackProxy_Region') || process.env.PRIVATE_KEY,
    certificate: config.getProperty('BackProxy_Region.sCertPath_BackProxy_Region') || process.env.CERTIFICATE,
    protocol: config.getProperty('sURLBackProxyRegionParts.protocol'),
    port: config.getProperty('sURLBackProxyRegionParts.port') || process.env.SSL_PORT
  },

  // List of user roles
  userRoles: ['guest', 'user', 'admin'],

  request: {
    debug: (config.getProperty('bDebug_Application') === "TRUE")
  },
  server: {
    session: {
      secret: config.getProperty('BackProxySession_Region.sSecret_BackProxySession_Region') || process.env.SESSION_SECRET,
      secure: (config.getProperty('BackProxySession_Region.bSecure_BackProxySession_Region')  === "TRUE") || process.env.SESSION_SECURE,
      maxAge: config.getProperty('BackProxySession_Region.nLiveMS_BackProxySession_Region') || process.env.SESSION_MAX_AGE
    }
  }
};

// Export the config object based on the NODE_ENV
// ==============================================

module.exports = all;
