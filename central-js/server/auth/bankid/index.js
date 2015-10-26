'use strict';

var express = require('express');
var passport = require('passport');
var auth = require('../auth.service');

var router = express.Router();

router.get('/', function (req, res, next) {
    passport.authenticate('oauth2', {
        callbackURL: '/auth/bankID/callback?link=' + req.query.link
    })(req, res, next);
});

router.get('/callback', function (req, res, next) {
    passport.authenticate('oauth2', {
        session: false,
        code: req.query.code,
        callbackURL: '/auth/bankID/callback?link=' + req.query.link
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
          res.redirect(req.query.link + '?error=' + JSON.stringify(error));
        } else {
          req.session.type = 'bankid';
          req.session.account = {
            firstName: user.customer.firstName,
            middleName: user.customer.middleName,
            lastName: user.customer.lastName
          };
          req.session.subject = user.subject;
          req.session.access = info;
          res.redirect(req.query.link);
        }
    })(req, res, next)
});

module.exports = router;
