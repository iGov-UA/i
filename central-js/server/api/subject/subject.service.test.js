'use strict';

module.exports.testData = {
  subject: {
    sID: "1111111111",
    sLabel: null,
    sLabelShort: null,
    nID: 11
  },

  sHost: {
    nID: 0,
    sID: "Common_Region",
    sType: "Region",
    sURL: "https://test.region.igov.org.ua/wf",
    sURL_Alpha: "",
    sURL_Beta: "",
    sURL_Omega: ""
  },

  subjectHuman: {
    "oSubject": {
      "sID": "2111111111", "sLabel": "Тестев Тест Тестович", "sLabelShort": "Тестев Т. Т.", "nID": 2
    }
    ,
    "sINN": "2222222222",
    "sSB": "333333333333333",
    "sPassportSeria": "ТТ",
    "sPassportNumber": "55555555",
    "sFamily": "Тест",
    "sSurname": "Тестович",
    "nID": 1,
    "sName": "Тест"
  }
  ,

  subjectOrgan: {
    "oSubject": {
      "sID": "ПАО", "sLabel": "ПАО Тест Тест", "sLabelShort": "Тест", "nID": 1
    }
    ,
    "sOKPO": "011111", "sFormPrivacy": "ПАО", "sNameFull": "Банк Тест", "nID": 1, "sName": "ТестБанк"
  }
}
;

