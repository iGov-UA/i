'use strict';

var appTest = require('../../app.spec');

appTest.centralNock
  .get('/wf/service/document/getDocumentTypes')
  .query(true)
  .reply(200, [{"bHidden": false, "nID": 21087, "sName": "test"}, {
    "bHidden": false,
    "nID": 21088,
    "sName": "test"
  }, {"bHidden": false, "nID": 21089, "sName": "testTest"}, {
    "bHidden": false,
    "nID": 0,
    "sName": "Квитанція про сплату"
  }, {"bHidden": false, "nID": 1, "sName": "Довідки/виписки, надані клієнтами в візові центри"}, {
    "bHidden": false,
    "nID": 2,
    "sName": "Громадянський паспорт"
  }, {"bHidden": false, "nID": 3, "sName": "ІПН"}])
  .get('/wf/service/document/getDocuments')
  .query(true)
  .reply(200, [{
    "sContentType": "false",
    "oSignData": "{}",
    "contentType": "false",
    "nID": 20051,
    "sName": "Паспорт",
    "oDocumentType": {"bHidden": false, "nID": 2, "sName": "Громадянський паспорт"},
    "sID_Content": "000000e0-3000-0a0b-9091-0bb000000d0c",
    "oDocumentContentType": {"nID": 2, "sName": "text/plain"},
    "sFile": "passport.zip",
    "sDate_Upload": "2015-07-05",
    "sID_Subject_Upload": "2000000001",
    "sSubjectName_Upload": "ПриватБанк",
    "oSubject_Upload": {"sID": "2000000001", "sLabel": null, "sLabelShort": null, "nID": 20049},
    "oSubject": {"sID": "2000000001", "sLabel": null, "sLabelShort": null, "nID": 20049}
  }], {
    'content-type': 'application/json;charset=UTF-8'
  });
