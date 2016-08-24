'use strict';

var nock = require('nock')
  , url = require('url')
  , urlencode = require('urlencode')
  , superagent = require('superagent')
  , supertest = require('supertest-as-promised')
  , app = require('./app')
  , testRequest = supertest(app)
  , async = require('async')
  , appData = require('./app.data.spec.js')
  , appTests = require('./app.tests.spec.js')(testRequest)
  , config = require('./config/environment')
//, config = require('./config')
  , bankidUtil = require('./auth/bankid/bankid.util.js');


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

var regionMock = nock('https://test.region.igov.org.ua')
  .persist()
  .log(console.log);


function getAuth(urlWithQueryParams, agentCallback, done) {
  testRequest
    .get(urlWithQueryParams)
    .expect(302)
    .then(function (res) {
      var loginAgent = superagent.agent();
      loginAgent.saveCookies(res);
      if (agentCallback) {
        agentCallback(loginAgent);
      }
      done();
    }).catch(function (err) {
    done(err)
  });
}

module.exports.loginWithBankID = function (done, agentCallback) {
  getAuth('/auth/bankid/callback?code=11223344&?link=' + testAuthResultURL, agentCallback, done);
};

module.exports.loginWithBankIDNBU = function (done, agentCallback, code) {
  testRequest
    .get('/auth/bankid-nbu?link=' + testAuthResultURL)
    .expect(302)
    .then(function (res) {
      var loginAgent = superagent.agent();
      loginAgent.saveCookies(res);

      var tokenRequest = testRequest.get('/auth/bankid-nbu/callback?code=' + code + '&?link=' + testAuthResultURL);
      loginAgent.attachCookies(tokenRequest);

      tokenRequest
        .expect(302)
        .then(function (res) {
          loginAgent.saveCookies(res);
          if (agentCallback) {
            agentCallback(loginAgent);
          }
          done();
        }).catch(function (err) {
        done(err)
      });
    });
};


module.exports.loginWithEds = function (done, agentCallback) {
  getAuth('/auth/eds/callback?code=11223344&link=' + testAuthResultURL, agentCallback, done);
};

module.exports.loginWithEmail = function (callback) {
  var code = 'ssss111';
  var email = 'test@test.com';
  var link = testAuthResultURL;
  var firstName = 'firstName';
  var lastName = 'lastName';
  var middleName = 'middleName';

  function prepareGet(url, agent) {
    var r = testRequest.get(url);
    if (agent) {
      agent.attachCookies(r);
    }
    return r;
  }

  function preparePost(url, agent) {
    var r = testRequest.post(url);
    if (agent) {
      agent.attachCookies(r);
    }
    return r;
  }

  function doGet(request, asyncCallback) {
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
        var loginAgent = superagent.agent();
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
      doPost(preparePost('/auth/email/verifyContactEmailAndCode', agent), {email: email, code: code}, asyncCallback);
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

module.exports.runModuleInitializationTest = function (name, moduleRequireFunction) {
  describe(name, function () {
    var module;
    before(function (done) {
      try {
        module = moduleRequireFunction();
      } catch (e) {
        //assert is in next test
      } finally {
        done();
      }
    });

    it(name + ' should be initialized', function (done) {
      if (module) {
        done();
      } else {
        done(name + ' is undefined')
      }
    });
  });
};

module.exports.app = app;
module.exports.bankidMock = bankidMock;
module.exports.centralNock = centralNock;
module.exports.regionMock = regionMock;
module.exports.authResultMock = authResultMock;
module.exports.testRequest = testRequest;
module.exports.tests = appTests;
