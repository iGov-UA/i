var Buffer = require('buffer').Buffer
  , passport = require('passport')
  , OAuth2Strategy = require('passport-oauth2')
  , OAuth2 = require('oauth').OAuth2
  , util = require('util')
  , _ = require('lodash')
  , bankidNBUUtil = require('./bankid.util')
  , bankidNBUService = require('./bankid.service');


exports.setup = function (config, authProviderRegistry) {
  function NBUOAuth2(clientId, clientSecret, baseSite, authorizePath, accessTokenPath, customHeaders) {
    OAuth2.call(this, clientId, clientSecret, baseSite, authorizePath, accessTokenPath, customHeaders);
  }

  util.inherits(NBUOAuth2, OAuth2);

  function NBUOAuth2Strategy(options, verify) {
    options = options || {};
    options.authorizationURL = options.authorizationURL;
    options.tokenURL = options.tokenURL;

    OAuth2Strategy.call(this, options, verify);

    this._oauth2 = new NBUOAuth2(options.clientID, options.clientSecret,
      '', options.authorizationURL, options.tokenURL, options.customHeaders);
    this.name = 'nbu-oauth2';
  }

  util.inherits(NBUOAuth2Strategy, OAuth2Strategy);

  NBUOAuth2Strategy.prototype.authorizationParams = function (options) {
    if (options.link) {
      return {state: new Buffer(options.link).toString('base64')}
    } else {
      return {};
    }
  };

  NBUOAuth2Strategy.prototype.tokenParams = function (options) {
    return {};
  };

  NBUOAuth2Strategy.prototype.userProfile = function (accessToken, done) {
    return bankidNBUService.syncWithSubject(accessToken, function (err, profile) {
      done(err, profile);
    });
  };

  authProviderRegistry.use('bankid-nbu', bankidNBUService);
  passport.use(new NBUOAuth2Strategy({
      authorizationURL: bankidNBUUtil.getAuthorizationURL(),
      tokenURL: bankidNBUUtil.getTokenURL(),
      clientID: config.bankidnbu.client_id,
      clientSecret: config.bankidnbu.client_secret
    },
    function (accessToken, refreshToken, subject, done) {
      done(null, subject, {
        accessToken: accessToken,
        refreshToken: refreshToken
      });
    }));
};
