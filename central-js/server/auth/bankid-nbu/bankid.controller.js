var passport = require('passport')
  , errors = require('../../components/errors')
  , authService = require('../auth.service');


module.exports.authorize = function (req, res, next) {
  req.session.prepare = authService.createPrepareSessionObject('bankid-nbu', {
    link: req.query.link
  });

  passport.authenticate('nbu-oauth2', {
    callbackURL: '/auth/bankid-nbu/callback',
    link: req.query.link
  })(req, res, next);
};

module.exports.token = function (req, res, next) {
  if (!req.session.prepare) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "session preparation should be initialized"));
  }
  if (req.session.prepare && req.session.prepare.type !== 'bankid-nbu') {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, "bankid-nbu type should be specified in session preparation"));
  }

  passport.authenticate('nbu-oauth2', {
    session: false,
    code: req.query.code,
    callbackURL: '/auth/bankid-nbu/callback',
    state: req.query.state
  }, function (err, user, info) {
    var error;

    var link = req.session.prepare.data.link;

    if (err) {
      error = {error: JSON.stringify(err)};
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
      res.redirect(link + '?error=' + JSON.stringify(error));
    } else {
      req.session = authService.createSessionObject('bankid-nbu', user, info);
      delete req.session.prepare;
      res.redirect(link);
    }
  })(req, res, next)
};
