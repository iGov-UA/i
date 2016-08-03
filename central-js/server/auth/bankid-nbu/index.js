'use strict';

var express = require('express')
  , Buffer = require('buffer').Buffer
  , passport = require('passport')
  , authService = require('../auth.service');

var router = express.Router();

router.get('/', function (req, res, next) {
  passport.authenticate('nbu-oauth2', {
    callbackURL: '/auth/bankid-nbu/callback',
    link: req.query.link
  })(req, res, next);
});

router.get('/callback', function (req, res, next) {
  passport.authenticate('nbu-oauth2', {
    session: false,
    code: req.query.code,
    callbackURL: '/auth/bankid-nbu/callback'
  }, function (err, user, info) {
    var error;

    var link = new Buffer(req.query.state, 'base64').toString('utf-8');

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
});

module.exports = router;
