'use strict';

var uuid = require('node-uuid')
  , regionMock = require('../api.region.nock.js').regionMock
  , centralMock = require('../api.central.nock.js').centralMock
  , uploadFileService = require('./uploadfile.service');

var headers = {
  'content-type': 'application/json;charset=UTF-8'
};

var endpoints = uploadFileService.getAPIEndpoints();

regionMock
  .post('/wf/service/' + endpoints.upload)
  .query(true)
  .reply(200, uuid.v1(), headers);

centralMock
  .post('/wf/service/' + endpoints.upload)
  .query(true)
  .reply(200, uuid.v1(), headers);

regionMock
  .get('/wf/service/' + endpoints.download)
  .query(true)
  .reply(200, {}, headers);//TODO multipart

centralMock
  .get('/wf/service/' + endpoints.download)
  .query(true)
  .reply(200, {}, headers);//TODO multipart
