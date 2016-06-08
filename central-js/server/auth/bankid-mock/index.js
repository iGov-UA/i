/**
 * Created by Igor on 5/6/2016.
 */
'use strict';

var express = require('express')
  , authService = require('../auth.service')
  , mock = require('../../api/user-mock/user.data.js');

var router = express.Router();

router.get('/', function (req, res, next) {
  //if (req.cookies['authMock'] && (req.headers.host.split(':')[0] === 'localhost' || req.headers.host.split(':')[0] === 'test.igov.org.ua')) {
  if (mock.isMockEnabled(req.cookies)) {
    var user = mock.user;
    var token = mock.token;

    req.session = authService.createSessionObject('bankid', user, token);
    delete req.session.prepare;
    res.redirect(req.query.link);
  } else {
    next();
  }
});

module.exports = router;
