/**
 * Created by Igor on 5/12/2016.
 */
'use strict';

var config = require('../../config/environment');


var user = {
  customer: {
    type: 'physical',
    clId: 'fe73790d63f2cec7d8e2a8d9ba0a2578b64e91bc',
    clIdText: 'Передана інформація є достовірною і підтверджена',
    lastName: 'MockUser',
    firstName: 'MockUser',
    middleName: 'MockUser',
    phone: '+380675668486',
    birthDay: '27.05.1985',
    inn: '3119325858',
    email: 'D.DUBILET@PRIVATBANK.UA',
    addresses: [{
      type: 'birth',
      country: 'UA',
      street: 'Нет улицы',
      dateModification: '29.05.2014 17:52:25.28'
    },
      {
        type: 'factual',
        country: 'UA',
        city: 'Київ',
        street: 'Григорія Царика',
        houseNo: '3',
        flatNo: '1',
        dateModification: '01.05.2016 06:43:22.55'
      }],
    documents: [{
      type: 'passport',
      series: 'АМ',
      number: '765369',
      issue: 'ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ',
      dateIssue: '18.03.2002',
      issueCountryIso2: 'UA'
    },
      {
        type: 'inn',
        series: 'АМ',
        number: '765369',
        issue: 'ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ',
        dateIssue: '18.03.2002',
        issueCountryIso2: 'UA'
      }],
    scans: [{
      type: 'passport',
      link: 'passport.pdf',
      extension: 'pdf',
      number: 1
    },
      {
        type: 'inn',
        link: 'inn.pdf',
        extension: 'pdf',
        number: 1
      }]
  },
  admin: {
    inn: '3119325858',
    token: 'bb35e70737d07355ef51351255424bac7b523694'
  },
  subject: {
    sID: '3119325858',
    sLabel: null,
    sLabelShort: null,
    aSubjectAccountContact: null,
    nID: 20045
  },
  usercacheid: 'a5644d69-dc86-440f-9773-a13f421d84b4'
};

var token = {
  access_token: "6d2bc0cf-d7c2-428d-9c7f-acd9a4e92fc3",
  token_type: "bearer",
  refresh_token: "6f6875a1-b78d-4b3b-a51a-05c77bbd8a24",
  expires_in: 179,
  scope: "read trust write"
};

module.exports.user = user;
module.exports.token = token;
module.exports.isMockEnabled = function (cookies) {
  return cookies['authMock'] && config.bTest;
};
//module.exports.isMockEnabled = function (cookies, host) {
//  return cookies['authMock'] && (host === 'localhost' || host === 'test.igov.org.ua');
//};
