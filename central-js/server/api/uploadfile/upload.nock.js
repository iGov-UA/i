//https://test.igov.org.ua/wf/service/object/file/upload_file_to_redis

var appTest = require('../../app.spec')
  , appData = require('../../app.data.spec');

var headers = {
  'content-type': 'application/json;charset=UTF-8'
};

var reqionFileID = 'region1111';
var centralFileID = 'central1111';

appTest
  .centralNock
  .post('/wf/service/object/file/upload_file_to_redis')
  .query(true)
  .reply(200, reqionFileID, headers)
  .get('/service/object/file/check_file_from_redis_sign')
  .query({sID_File_Redis: 1, nID_Subject: 11})
  .reply(200, appData.signCheck, {
    'Content-Type': 'application/json'
  })
  .get('/service/object/file/check_file_from_redis_sign')
  .query({sID_File_Redis: 2, nID_Subject: 11})
  .reply(200, appData.signCheckError, {
    'Content-Type': 'application/json'
  });

appTest
  .regionMock
  .post('/wf/service/object/file/upload_file_to_redis')
  .query(true)
  .reply(200, centralFileID, headers);



