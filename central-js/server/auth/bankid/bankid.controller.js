var passport = require('passport')
  , errors = require('../../components/errors')
  , authService = require('../auth.service');

module.exports.authenticate = function (req, res, next) {
  passport.authenticate('oauth2', {
    callbackURL: '/auth/bankID/callback?link=' + req.query.link
  })(req, res, next);
};

module.exports.token = function (req, res, next) {
  passport.authenticate('oauth2', {
    session: false,
    code: req.query.code,
    callbackURL: '/auth/bankID/callback?link=' + req.query.link
  }, function (err, user, info) {
    var error;

    if (err) {
      error = err;
    } else if (!info.accessToken) {
      error = {error: 'Cant find acess token. Something went wrong, please try again.'};
    } else if (info.accessToken.oauthError) {
      error = {error: info.accessToken.message + ' ' + info.accessToken.oauthError.message};
    } else if (!info.refreshToken) {
      error = {error: 'Cant find refresh token. Something went wrong, please try again.'};
    } else if (!user) {
      error = {error: 'Cant sync user'};
    }


    if (error) {
      var errString = encodeURIComponent(JSON.stringify(error));
      console.log('bankid authentication error : ' + errString);
      res.redirect(req.query.link + '?error=' + errString);
    } else {
      req.session = authService.createSessionObject('bankid', user, info);
      delete req.session.prepare;
      res.redirect(req.query.link);
    }
  })(req, res, next);
};
