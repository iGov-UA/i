'use strict';

var nock = require('nock');
var url = require('url');
var urlencode = require('urlencode');
var testRequest = require('supertest-as-promised');
var request = require('request');

var app = require('./app');
var appData = require('./app.data.spec.js');
var config = require('./config/environment');
var bankidUtil = require('./auth/bankid/bankid.util.js');
var agent = testRequest(app);

var pathFromURL = function (urlString) {
  return urlString.split(/\?/).filter(function (item, i) {
    return i == 0
  }).reduce(function (previous, item) {
    return item
  })
};
var queryStringToObject = function (urlString) {
  return urlString.split(/&|\?/g)
    .filter(function (item, i) {
      return i > 0
    }).reduce(function (toObject, item) {
      var pair = item.split(/=/);
      toObject[pair[0]] = pair[1];
      return toObject;
    }, {});
};

var baseUrls = bankidUtil.getBaseURLs();

var testAuthResultPath = '/auth/result';
var testAuthResultBase = 'http://localhost:9001';
var testAuthResultURL = testAuthResultBase + testAuthResultPath;

var authResultMock = nock(testAuthResultBase)
  .persist()
  .log(console.log)
  .get(testAuthResultPath)
  .query(true)
  .reply(200, function (uri, requestBody) {
    var query = queryStringToObject(uri);
    for (var key in query) {
      query[key] = urlencode.decode(query[key]);
    }
    return query;
  }, {
    'content-type': 'application/json;charset=UTF-8',
  });

var bankidMock = nock(baseUrls.access.base)
  .persist()
  .log(console.log)
  .get(baseUrls.access.path.auth)
  .query(true)
  .reply(302, {}, {
    'Location': function (req) {
      var query = queryStringToObject(req.path);
      var redirect = urlencode.decode(query.redirect_uri);
      var baseURL = pathFromURL(redirect);
      var redirectQuery = queryStringToObject(redirect);
      var result = baseURL + '?link=' + urlencode.encode(redirectQuery.link) + '&code=112233';
      var path = url.parse(result).path;
      return 'http://localhost:9000' + path;
    }
  })
  .post(baseUrls.access.path.token)
  .query(true)
  .reply(200, appData.token, {
    'content-type': 'application/json;charset=UTF-8',
    'access-control-allow-origin': '*',
    'access-control-allow-headers': 'Authorization, content-type',
    'access-control-allow-methods': 'GET, OPTIONS, POST',
    'access-control-allow-credentials': 'true'
  })
  .post(baseUrls.resource.path.info)
  .reply(200, {
    "state": "ok",
    "customer": appData.customer
  }, {
    'content-type': 'application/json;charset=UTF-8',
    'cache-control': 'no-cache, no-store, max-age=0, must-revalidate',
    pragma: 'no-cache',
    expires: '0',
    'access-control-allow-origin': '*',
    'access-control-allow-headers': 'Authorization, content-type',
    'access-control-allow-methods': 'GET, OPTIONS, POST',
    'access-control-allow-credentials': 'true'
  });

nock('https://test.igov.org')
  .persist()
  .log(console.log)
  .get('/wf/service/subject/syncSubject')
  .query(true)
  .reply(200, appData.syncedCustomer, {
    'content-type': 'application/json;charset=UTF-8',
    'strict-transport-security': 'max-age=31536000'
  });

module.exports.authAndTest = function(callback){
  agent
    .get('/auth/bankid?link=' + testAuthResultURL)
    .expect(302)
    .then(function (res) {
      request(res.headers.location, function (error, response, body) {
        body = JSON.parse(body);
        if (!error && !body.error && response.statusCode == 200) {
          callback();
        } else if (error){
          callback(error);
        } else if (body.error){
          callback(body.error);
        }
      });
    }).catch(function (err) {
      callback(err)
    });
};

module.exports.app = app;
module.exports.bankidMock = bankidMock;
module.exports.authResultMock = authResultMock;
module.exports.agent = agent;
