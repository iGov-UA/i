var passport = require('passport')
  , errors = require('../../components/errors')
  , authService = require('../auth.service')
  , logger = require('../../components/logger').createLogger(module);

module.exports.authenticate = function (req, res, next) {
  logger.info('authenticate bankid request', { query : req.query });
  passport.authenticate('oauth2', {
    callbackURL: '/auth/bankID/callback?link=' + req.query.link
  })(req, res, next);
};

module.exports.token = function (req, res, next) {
  logger.info('token bankid request', { query : req.query });
  passport.authenticate('oauth2', {
    session: false,
    code: req.query.code,
    callbackURL: '/auth/bankID/callback?link=' + req.query.link
  }, function (err, user, info) {
    logger.info('token bankid result', { error : err, user : user, info : info });
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
      logger.info('token bankid error, redirect back to initial page', { error : error, link : req.query.link });
      res.redirect(req.query.link + '?error=' + errString);
    } else {
      var session = authService.createSessionObject('bankid', user, info);
      req.session = session;
      delete req.session.prepare;
      logger.info('bankid session is created', session);
      logger.info('token bankid success, redirect back to initial page', { link : req.query.link });
      res.redirect(req.query.link);
    }
  })(req, res, next);
};
