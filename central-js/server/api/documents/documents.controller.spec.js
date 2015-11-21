'use strict';

var should = require('should');
var app = require('../../app');
var request = require('supertest');

nock('https://test.igov.org.ua:443')
  .get('/wf/service/services/getDocumentTypes')
  .query({"nID_Subject": "20049"})
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
  }, {"bHidden": false, "nID": 3, "sName": "ІПН"}]);

nock('https://test.igov.org.ua:443')
  .get('/wf/service/services/getDocuments')
  .query({"nID_Subject": "20049"})
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
    server: 'nginx',
    date: 'Fri, 20 Nov 2015 21:14:37 GMT',
    'content-type': 'application/json;charset=UTF-8',
    'content-length': '594',
    connection: 'close',
    'strict-transport-security': 'max-age=31536000'
  });

nock('https://test.igov.org.ua:443')
  .get('/wf/service/subject/syncSubject')
  .query({"sINN":"2943209693"})
  .reply(200, {"sID":"2943209693","sLabel":null,"sLabelShort":null,"nID":20049}, { server: 'nginx',
    date: 'Fri, 20 Nov 2015 21:14:33 GMT',
    'content-type': 'application/json;charset=UTF-8',
    'content-length': '65',
    connection: 'close',
    'strict-transport-security': 'max-age=31536000' });


describe('POST /api/documents/initialUpload', function () {
  it('should respond with 200', function (done) {
    request(app)
      .post('/api/service/documents/initialUpload')
      .send([{nID: 2, sName: 'Паспорт'}])
      .expect(200)
      .expect('Content-Type', /json/)
      .end(function (err, res) {
        if (err) return done(err);

        done();
      });
  });
});
