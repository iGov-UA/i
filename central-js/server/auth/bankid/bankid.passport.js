var passport = require('passport')
  , OAuth2Strategy = require('passport-oauth2')
  , crypto = require('crypto')
  , bankidUtil = require('./bankid.util')
  , bankidService = require('./bankid.service')
  , logger = require('../../components/logger').createLogger(module);


exports.setup = function (config, authProviderRegistry) {
  function BankIDAuth() {

  }

  BankIDAuth.prototype = new OAuth2Strategy({
      authorizationURL: bankidUtil.getAuthorizationURL(),
      tokenURL: bankidUtil.getTokenURL(),
      clientID: config.bankid.client_id,
      clientSecret: config.bankid.client_secret
    },
    function (accessToken, refreshToken, subject, done) {
      done(null, subject, {
        accessToken: accessToken,
        refreshToken: refreshToken
      });
    });

  BankIDAuth.prototype.authorizationParams = function (options) {
    if (options.eds) {
      return {eds: true};
    } else if (options.mpbds) {
      return {bank: 'mpbds'};
    } else {
      return {};
    }
  };

  BankIDAuth.prototype.tokenParams = function (options) {
    var unhashed = config.bankid.client_id +
      config.bankid.client_secret + options.code;

    var clientSecretHashed = crypto.createHash('sha1').update(unhashed).digest('hex');
    var params = {};
    Object.defineProperty(params, 'client_secret', {
      value: clientSecretHashed,
      writable: false,
      enumerable: true,
      configurable: false
    });

    return params;
  };

  BankIDAuth.prototype.userProfile = function (accessToken, done) {
    return bankidService.syncWithSubject(accessToken, function (err, profile) {
      done(err, profile);
    });
  };

  authProviderRegistry.use('bankid', bankidService);
  authProviderRegistry.use('eds', bankidService);
  authProviderRegistry.use('mpbds', bankidService);
  passport.use(new BankIDAuth());
};
