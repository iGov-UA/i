'use strict';

var appTest = require('../../app.spec');

var headers = {
  'content-type': 'application/json;charset=UTF-8'
};

//{
//  server: 'nginx',
//    date: 'Thu, 03 Mar 2016 20:56:01 GMT',
//  'content-type': 'application/json;charset=UTF-8',
//  'content-length': '32987',
//  connection: 'close',
//  'strict-transport-security': 'max-age=31536000'
//}https://test.igov.org.ua/wf/service/action/item/getServicesTree
appTest
  .centralNock
  .get('/wf/service/action/item/getServicesTree')
  .query(true)
  .reply(200, [{
      "nID": 1,
      "sName": "Громадянам",
      "sID": "Citizen",
      "nOrder": 1,
      "aSubcategory": [{
        "nID": 3,
        "sName": "Поліція",
        "sID": "Police",
        "nOrder": 1,
        "aService": [{
          "nOpenedLimit": 0,
          "sSubjectOperatorName": "Міністерство внутрішніх справ",
          "openedLimit": 0,
          "subjectOperatorName": "Міністерство внутрішніх справ",
          "nID": 1,
          "sName": "Надання довідки про притягнення до кримінальної відповідальності, відсутність (наявність) судимості або обмежень, передбачених кримінально-процесуальним законодавством України",
          "nOrder": 1,
          "nSub": 7,
          "nID_Status": 0,
          "nStatus": 2
        }, {
          "nOpenedLimit": 0,
          "sSubjectOperatorName": "Міністерство внутрішніх справ",
          "openedLimit": 0,
          "subjectOperatorName": "Міністерство внутрішніх справ",
          "nID": 1397,
          "sName": "Надання витягу з Єдиного державного реєстру МВС",
          "nOrder": 2,
          "nSub": 1,
          "nID_Status": 0,
          "nStatus": 2
        }]
      }]
    }],
    headers
  );
