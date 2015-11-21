var passport = require('passport');
var OAuth2Strategy = require('passport-oauth2');
var crypto = require('crypto');
var url = require('url');
var bankidUtil = require('./bankid.util.js');

exports.setup = function (config, accountService) {
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
        return options.eds ? {eds : true} : {};
    };

    BankIDAuth.prototype.tokenParams = function (options) {
        var unhashed = config.bankid.client_id +
            config.bankid.client_secret + options.code;

        var clientSecretHashed = crypto.createHash('sha1').update(unhashed).digest('hex');
        var params = {};
        Object.defineProperty(params, 'client_secret', {
            value: clientSecretHashed,
            writable : false,
            enumerable : true,
            configurable : false
        });

        return params;
    };

    BankIDAuth.prototype.userProfile = function(accessToken, done){
        return accountService.syncWithSubject(accessToken, function (err, profile) {
            done(err, profile);
        });
    };

    passport.use(new BankIDAuth());
};
