var passport = require('passport');
var OAuth2Strategy = require('passport-oauth2');
var OAuth2 = require('oauth').OAuth2;
var util = require('util');
var _ = require('lodash');
var soccardUtil = require('./soccard.util');
var soccardService = require('./soccard.service');

exports.setup = function (config) {
  function KCOAuth2(clientId, clientSecret, baseSite, authorizePath, accessTokenPath, customHeaders) {
    OAuth2.call(this, clientId, clientSecret, baseSite, authorizePath, accessTokenPath, customHeaders);
  }

  util.inherits(KCOAuth2, OAuth2);

  KCOAuth2.prototype._requestFromOauth2 = KCOAuth2.prototype._request;

  KCOAuth2.prototype._request = function (method, url, headers, post_body, access_token, callback) {
    headers = soccardUtil.addTransactionHeader(headers);
    headers = soccardUtil.addSignHeader(headers, config, headers['SocCard-API-Transaction-ID'], method,
                                                url, post_body, headers['Content-Type']);
    this._requestFromOauth2(method, url, headers, post_body, access_token, callback);
  };

  function KCOAuth2Strategy(options, verify) {
    options = options || {};
    options.authorizationURL = options.authorizationURL;
    options.tokenURL = options.tokenURL;

    OAuth2Strategy.call(this, options, verify);

    this._oauth2 = new KCOAuth2(options.clientID, options.clientSecret,
      '', options.authorizationURL, options.tokenURL, options.customHeaders);
    this.name = 'soccard-oauth2';
  }

  util.inherits(KCOAuth2Strategy, OAuth2Strategy);

  KCOAuth2Strategy.prototype.authorizationParams = function (options) {
    return {};
  };

  KCOAuth2Strategy.prototype.tokenParams = function (options) {
    return {};
  };

  KCOAuth2Strategy.prototype.userProfile = function (accessToken, done) {
    return soccardService.syncWithSubject(accessToken, function (err, profile) {
      done(err, profile);
    });
  };

  passport.use(new KCOAuth2Strategy({
      authorizationURL: soccardUtil.getAuthorizationURL(config),
      tokenURL: soccardUtil.getTokenURL(config),
      clientID: config.soccard.socCardAPIClientID,
      clientSecret: config.soccard.socCardAPIClientSecret,
      customHeaders: {
        'SocCard-API-Client-ID': config.soccard.socCardAPIClientID,
        'SocCard-API-Version': config.soccard.socCardAPIVersion
      }
    },
    function (accessToken, refreshToken, subject, done) {
      done(null, subject, {
        accessToken: accessToken,
        refreshToken: refreshToken
      });
    }));
};
