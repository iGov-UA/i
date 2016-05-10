/**
 * Created by Igor on 5/6/2016.
 */
'use strict';

var express = require('express')
  , authService = require('../auth.service');

var router = express.Router();

router.get('/', function (req, res, next) {
  if (req.cookies['authMock'] && (req.headers.host.split(':')[0] === 'localhost' || req.headers.host.split(':')[0] === 'test.igov.org.ua')) {
    var user = {
      customer: {
        type: 'physical',
        clId: '1',
        clIdText: '',
        lastName: 'MockUser',
        firstName: req.cookies['authMock'],
        middleName: 'MockUser',
        phone: '+380101010101',
        birthDay: '',
        inn: '1',
        email: 'Mock@PRIVATBANK.UA',
        addresses: [[Object], [Object]],
        documents: [[Object]],
        scans: [[Object]]
      },
      admin: {
        inn: '1',
        token: 'a'
      },
      subject: {
        sID: '1',
        sLabel: null,
        sLabelShort: null,
        aSubjectAccountContact: null,
        nID: 20045
      },
      usercacheid: 'a'
    };
    req.session = authService.createSessionObject('bankIDMock', user, {});
    res.redirect(req.query.link);
  } else {
    next();
  }
  //res.send(req.cookies);
  //req.session = authService.createSessionObject('bankIDMock', user, info);
  //res.redirect(req.query.link);
});

module.exports = router;
