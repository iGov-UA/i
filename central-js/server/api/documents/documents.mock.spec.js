'use strict';

var appTest = require('../../app.spec');

appTest.centralNock
  .get('/wf/service/services/getDocumentTypes')
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
  .get('/wf/service/services/getDocuments')
  .query(true)
  .reply(200, [{
    "sContentType": "false",
    "oSignData": "{}",
    "contentType": "false",
    "nID": 20051,
    "sName": "Паспорт",
    "oDocumentType": {"bHidden": false, "nID": 2, "sName": "Громадянський паспорт"},
    "sID_Content": "023454e8-3089-4a5b-9091-1bb577483d5c",
    "oDocumentContentType": {"nID": 2, "sName": "text/plain"},
    "sFile": "passport.zip",
    "sDate_Upload": "2015-07-05",
    "sID_Subject_Upload": "2943209693",
    "sSubjectName_Upload": "Приватбанк",
    "oSubject_Upload": {"sID": "2943209693", "sLabel": null, "sLabelShort": null, "nID": 20049},
    "oSubject": {"sID": "2943209693", "sLabel": null, "sLabelShort": null, "nID": 20049}
  }], {
    'content-type': 'application/json;charset=UTF-8',
  });
