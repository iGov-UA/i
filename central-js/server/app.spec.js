'use strict';

var nock = require('nock');
var url = require('url');
var urlencode = require('urlencode');
var superagent = require('superagent');
var supertest = require('supertest-as-promised');
var app = require('./app');
var appData = require('./app.data.spec.js');
var config = require('./config/environment');
var bankidUtil = require('./auth/bankid/bankid.util.js');
var testRequest = supertest(app);
var loginAgent = superagent.agent();
var async = require('async');


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

var centralNock = nock('https://test.igov.org.ua')
  .persist()
  .log(console.log);

var regionMock = nock('http://test.region.service')
  .persist()
  .log(console.log)
  .get('/service/object/file/check_file_from_redis_sign')
  .query({sID_File_Redis: 1, nID_Subject: 20049})
  .reply(200, appData.signCheck, {
    'Content-Type': 'application/json'
  })
  .get('/service/object/file/check_file_from_redis_sign')
  .query({sID_File_Redis: 2, nID_Subject: 20049})
  .reply(200, appData.signCheckError, {
    'Content-Type': 'application/json'
  });


module.exports.loginWithBankID = function (callback) {
  testRequest
    .get('/auth/bankid/callback?code=11223344&?link=' + testAuthResultURL)
    .expect(302)
    .then(function (res) {
      loginAgent.saveCookies(res);
      callback(null, loginAgent);
    }).catch(function (err) {
    callback(err)
  });
};

module.exports.loginWithEds = function (callback) {
  testRequest
    .get('/auth/eds/callback?code=11223344&link=' + testAuthResultURL)
    .expect(302)
    .then(function (res) {
      loginAgent.saveCookies(res);
      callback(null, loginAgent);
    }).catch(function (err) {
    callback(err)
  });
};

module.exports.loginWithEmail = function (callback) {
  var code = 'ssss111';
  var email = 'test@test.com';
  var link = testAuthResultURL;
  var firstName = 'firstName';
  var lastName = 'lastName';
  var middleName = 'middleName';

  function prepareGet(url, agent){
    var r = testRequest.get(url);
    if(agent){
      agent.attachCookies(r);
    }
    return r;
  }

  function preparePost(url, agent){
    var r = testRequest.post(url);
    if(agent){
      agent.attachCookies(r);
    }
    return r;
  }

  function doGet(request, asyncCallback){
    request
      .expect(302)
      .then(function (res) {
        loginAgent.saveCookies(res);
        asyncCallback(null, loginAgent);
      })
      .catch(function (err) {
        asyncCallback(err, null);
      });
  }

  function doPost(request, body, asyncCallback) {
    request
      .send(body)
      .expect(200)
      .then(function (res) {
        loginAgent.saveCookies(res);
        asyncCallback(null, loginAgent);
      })
      .catch(function (err) {
        asyncCallback(err, null);
      });
  }

  async.waterfall([
    function (asyncCallback) {
      doPost(preparePost('/auth/email/verifyContactEmail'), {email: email, link: link}, asyncCallback);
    },
    function (agent, asyncCallback) {
      doPost(preparePost('/auth/email/verifyContactEmailAndCode',agent), {email: email, code: code}, asyncCallback);
    },
    function (agent, asyncCallback) {
      doPost(preparePost('/auth/email/editFio', agent), {
        firstName: firstName,
        lastName: lastName,
        middleName: middleName
      }, asyncCallback);
    },
    function (agent, asyncCallback) {
      doGet(prepareGet('/auth/email', agent), asyncCallback);
    }
  ], function (error, result) {
    callback(error, result);
  });
};

module.exports.app = app;
module.exports.bankidMock = bankidMock;
module.exports.centralNock = centralNock;
module.exports.regionMock = regionMock;
module.exports.authResultMock = authResultMock;
module.exports.testRequest = testRequest;
