'use strict';

var session = require('supertest-session');
var app = require('./app');
var authService = require('./auth/auth.service');

var createSuperTestSession = function (type, user, access) {
  return session(app, {
    before: function (req) {
      req.set('authorization', 'Basic aGVsbG86d29ybGQK');
      req.session = authService.createSessionObject(type, user, access);
    }
  });
};

module.exports.createSuperTestWithCustomSession = function (type, user, access) {
  return createSuperTestSession(type, user, access);
};

module.exports.createSuperTestWithDefaultSession = function () {
  return createSuperTestSession('bankid', {
    customer: {
      "type": "physical",
      "clId": "111111dfdfd22222",
      "clIdText": "Передана інформація є достовірною і підтверджена BankID 26.10.2015 23:23",
      "lastName": "TEST lastName",
      "firstName": "TEST firstName",
      "middleName": "TEST middleName",
      "phone": "+380681112233",
      "birthDay": "01.01.1900",
      "inn": "0000000001",
      "email": "TEST@EMAIL.COM",
      "addresses": [{
        "type": "factual",
        "country": "UA",
        "state": "ДНЕПРОПЕТРОВСКАЯ",
        "city": "ДНЕПРОПЕТРОВСК",
        "street": "нет улицы",
        "houseNo": "00",
        "flatNo": "00",
        "dateModification": "26.03.2015 13:44:10.706"
      }, {
        "type": "birth",
        "country": "UA",
        "state": "ДНЕПРОПЕТРОВСКАЯ",
        "city": "ДНЕПРОПЕТРОВСК",
        "street": "Нет улицы",
        "dateModification": "16.01.2015 15:11:24.24"
      }],
      "documents": [{
        "type": "passport",
        "series": "TT",
        "number": "000001",
        "issue": "БАБУШКИНСКИМ РО ДГУ УМВД",
        "dateIssue": "01.01.1996",
        "issueCountryIso2": "UA"
      }]
    },
    subject: {}
  }, {
    accessToken: 'fsdfsdfsdfsf',
    refreshToken: 'sdfsdfsdfsfsdfsdf'
  });
};
