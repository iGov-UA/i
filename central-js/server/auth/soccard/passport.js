var passport = require('passport');
var OAuth2Strategy = require('passport-oauth2');
var OAuth2 = require('oauth').OAuth2;
var crypto = require('crypto');
var util = require('util');
var _ = require('lodash');
var uuid = require('node-uuid');

exports.setup = function (config, url, accountService) {

  var authorizationURL = url.format({
    protocol: config.soccard.socCardAPIProtocol,
    hostname: config.soccard.socCardAPIHostname,
    pathname: '/api/oauth'
  });

  var tokenURL = url.format({
    protocol: config.soccard.socCardAPIProtocol,
    hostname: config.soccard.socCardAPIHostname,
    pathname: '/api/oauth/token'
  });

  function KCOAuth2(clientId, clientSecret, baseSite, authorizePath, accessTokenPath, customHeaders) {
    OAuth2.call(this, clientId, clientSecret, baseSite, authorizePath, accessTokenPath, customHeaders);
  }

  util.inherits(KCOAuth2, OAuth2);

  KCOAuth2.prototype.signData = function (method, url, headers, post_body) {
    //
    //HTTP Method У верхньому регістрі. Наприклад: POST, GET
    //HTTP Request URI Шлях до ресурсу. Наприклад, /oauth/token
    //HostІз заголовку запиту.Наприклад, test.kyivcard.com.ua
    //Port Порт з’єднання. Якщо порт відсутній в Host, то встановлюється порт по замовчуванню («80» для http та «443» для https)
    //SocCard-API-Version Із заголовку запиту
    //SocCard-API-Transaction-ID Із заголовку запиту
    //Content-Type Із заголовку запиту
    //Content Тіло запиту. Наприклад, grant_type=refresh_token &refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
    return 'TODO signed data here';
  };

  KCOAuth2.prototype._requestFromOauth2 = KCOAuth2.prototype._request;

  KCOAuth2.prototype._request = function (method, url, headers, post_body, access_token, callback) {
    headers = _.merge(headers,
      {
        'SocCard-API-Transaction-ID': uuid.v1(),
        'SocCard-API-Signature': this.signData(method, url, headers, post_body)
      }
    );
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

  };

  passport.use(new KCOAuth2Strategy({
      authorizationURL: authorizationURL,
      tokenURL: tokenURL,
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
