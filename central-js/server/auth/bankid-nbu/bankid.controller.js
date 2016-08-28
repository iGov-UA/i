var passport = require('passport')
  , errors = require('../../components/errors')
  , authService = require('../auth.service');


module.exports.authenticate = function (req, res, next) {
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
    res.status(400).send(errors.createInputParameterError(
      "session preparation should be initialized"));
    return;
  }
  if (req.session.prepare && req.session.prepare.type !== 'bankid-nbu') {
    res.status(400).send(errors.createInputParameterError(
      "bankid-nbu type should be specified in session preparation"));
    return;
  }
  if(!req.session.prepare.data ){
    res.status(400).send(errors.createInputParameterError(
      "session preparation data for bankid-nbu session type should exist"));
    return;
  }
  if(req.session.prepare.data && !req.session.prepare.data.link){
    res.status(400).send(errors.createInputParameterError(
      "session preparation data for bankid-nbu session type doesn't contain link"));
    return;
  }

  var link = req.session.prepare.data.link;

  passport.authenticate('nbu-oauth2', {
    session: false,
    code: req.query.code,
    callbackURL: '/auth/bankid-nbu/callback',
    state: req.query.state
  }, function (err, user, info) {
    var error;

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
      var errorString = JSON.stringify(error);
      console.log(errorString);//TODO replace with real logger
      res.redirect(link + '?error=' + errorString);
    } else {
      req.session = authService.createSessionObject('bankid-nbu', user, info);
      delete req.session.prepare;
      res.redirect(link);
    }
  })(req, res, next)
};
