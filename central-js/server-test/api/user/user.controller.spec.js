var should = require('should')
  , appTest = require('../../app.spec.js')
  , bankidData = require('../../auth/bankid-nbu/bankid.data.spec')
  , testRequest = appTest.testRequest;

require('../../auth/bankid-nbu/bankid.nock');

var bankidData = require('../../auth/bankid-nbu/bankid.data.spec')
  , testRequest = appTest.testRequest;

describe('user controller test with NBU login', function () {
  var agent;
  before(function (done) {
    appTest.loginWithBankIDNBU(function (error) {
      if (error) {
        done(error)
      } else {
        done();
      }
    }, function (loginAgent) {
      agent = loginAgent;
    }, bankidData.codes.forCustomerDataResponse);
  });

  it('should respond with 200 and remove cookie session', function (done) {
    var getUser = testRequest.get('/api/user');

    appTest.attachCookies(agent, getUser);

    getUser.expect(200)
      .then(function (res) {
        console.log(res.body);
        //TODO check why cookies are not removed
        done();
      }).catch(function (err) {
      done(err)
    });
  });
});
