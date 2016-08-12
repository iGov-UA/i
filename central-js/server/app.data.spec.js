'use strict';

module.exports.token = {
  access_token: "caef896e-3d84-4c16-8a2b-f4f2264db6b1",
  token_type: "bearer",
  refresh_token: "5fd85cd8-2e71-4921-b102-eecfb0024e1a",
  expires_in: 179,
  scope: "read trust write"
};

module.exports.syncedCustomer = {
  sID: "2943209693",
  sLabel: null,
  sLabelShort: null,
  nID: 20049
};

module.exports.customerShort = {
  "type": "physical",
  "clId": "111111dfdfd22222"
};

module.exports.customer = {
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
};

module.exports.signCheckError = {
  "code": "SYSTEM_ERR",
  "message": "File with sID_File_Redis 'd2993755-70e5-409e-85e5-46ba8ce98e1e' not found."
};

module.exports.signCheckNoSign = {};

module.exports.signCheck = {
  "state": "ok",
  "customer": {
    "inn": "1436057000",
    "fullName": "Сервіс зберігання сканкопій",
    "signatureData": {
      "name": "АЦСК ПАТ КБ «ПРИВАТБАНК»",
      "serialNumber": "0D84EDA1BB9381E80400000079DD02004A710800",
      "timestamp": "29.10.2015 13:45:33",
      "code": true,
      "desc": "ПІДПИС ВІРНИЙ",
      "dateFrom": "13.08.2015 11:24:31",
      "dateTo": "12.08.2016 23:59:59",
      "sn": "UA-14360570-1"
    },
    "organizations": [{
      "type": "edsOwner",
      "name": "ПАТ КБ «ПРИВАТБАНК»",
      "mfo": "14360570",
      "position": "Технологічний сертифікат",
      "ownerDesc": "Співробітник банку",
      "address": {"type": "factual", "state": "Дніпропетровська", "city": "Дніпропетровськ"}
    }, {
      "type": "edsIsuer",
      "name": "ПУБЛІЧНЕ АКЦІОНЕРНЕ ТОВАРИСТВО КОМЕРЦІЙНИЙ БАНК «ПРИВАТБАНК»",
      "unit": "АЦСК",
      "address": {"type": "factual", "state": "Дніпропетровська", "city": "Дніпропетровськ"}
    }]
  }
};
