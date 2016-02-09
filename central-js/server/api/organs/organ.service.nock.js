'use strict';

var appTest = require('../../app.spec');

var dataSubjectOrgan = {
  "oSubject": {
    "sID": "ПАО", "sLabel": "ПАО Тест Тест", "sLabelShort": "Тест", "nID": 1
  }
  ,
  "sOKPO": "011111", "sFormPrivacy": "ПАО", "sNameFull": "Банк Тест", "nID": 1, "sName": "ТестБанк"
};

appTest
  .centralNock
  .get('/wf/service/subject/getSubjectOrgan')
  .query(true)
  .reply(200, dataSubjectOrgan, {
    'content-type': 'application/json;charset=UTF-8'
  });

var dataSubjectHuman = {
  "oSubject": {
    "sID": "2111111111", "sLabel": "Тестев Тест Тестович", "sLabelShort": "Тестев Т. Т.", "nID": 2
  },
  "sINN": "2222222222",
  "sSB": "333333333333333",
  "sPassportSeria": "ТТ",
  "sPassportNumber": "55555555",
  "sFamily": "Тест",
  "sSurname": "Тестович",
  "nID": 1,
  "sName": "Тест"
};


appTest
  .centralNock
  .get('/wf/service/subject/getSubjectHuman')
  .query(true)
  .reply(200, dataSubjectHuman, {
    'content-type': 'application/json;charset=UTF-8'
  });

