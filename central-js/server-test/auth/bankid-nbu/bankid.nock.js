var nock = require('nock')
  , url = require('url')
  , urlencode = require('urlencode')
  , appUtil = require('../../app.util.spec.js')
  , bankidNBUData = require('./bankid.data.spec.js')
  , bankidNBUUtil = require('./../../../server/auth/bankid-nbu/bankid.util.js');

var baseUrls = bankidNBUUtil.getBaseURLs();

var headers = {
  'content-type': 'application/json;charset=UTF-8'
};

var bankidNBUMock = nock(baseUrls.access.base)
  .persist()
  .log(console.log)
  .get(baseUrls.access.path.auth)
  .query(true)
  .reply(302, {}, {
    'Location': function (req) {
      var query = appUtil.queryStringToObject(req.path);
      var redirect = urlencode.decode(query.redirect_uri);
      var baseURL = appUtil.pathFromURL(redirect);
      var redirectQuery = appUtil.queryStringToObject(redirect);
      var result = baseURL + '?link=' + urlencode.encode(redirectQuery.link) + '&code=112233';
      var path = url.parse(result).path;
      return 'http://localhost:9000' + path;
    }
  });

bankidNBUMock
  .post(baseUrls.access.path.token, function (body) {
    return body && body.code === bankidNBUData.codes.forCustomerDataResponse;
  })
  .reply(200, bankidNBUData.createToken(bankidNBUData.accessTokens.forCustomerDataResponse), headers)
  .post(baseUrls.access.path.token, function (body) {
    return body && body.code === bankidNBUData.codes.forCustomerDataCryptoResponse;
  })
  .reply(200, bankidNBUData.createToken(bankidNBUData.accessTokens.forCustomerDataCryptoResponse), headers)
  .post(baseUrls.access.path.token, function (body) {
    return body && body.code === bankidNBUData.codes.forErrorResponse406;
  })
  .reply(200, bankidNBUData.createToken(bankidNBUData.accessTokens.forErrorResponse406), headers)
  .post(baseUrls.access.path.token, function (body) {
    return body && body.code === bankidNBUData.codes.forErrorResponse501;
  })
  .reply(200, bankidNBUData.createToken(bankidNBUData.accessTokens.forErrorResponse501), headers)

  .post(baseUrls.resource.path.info)
  .matchHeader('Authorization', function (val) {
    return appUtil.extractAccessTokenBankIDNBU(val) === bankidNBUData.accessTokens.forCustomerDataResponse;
  })
  .reply(200, bankidNBUData.customerData, headers)
  .post(baseUrls.resource.path.info)
  .matchHeader('Authorization', function (val) {
    return appUtil.extractAccessTokenBankIDNBU(val) === bankidNBUData.accessTokens.forCustomerDataCryptoResponse;
  })
  .reply(200, bankidNBUData.createEncryptedCustomer(bankidNBUData.customerData,
    bankidNBUUtil, __dirname + '/../../../iGov_sgn_cert.pem'), headers)
  .post(baseUrls.resource.path.info)
  .matchHeader('Authorization', function (val) {
    return appUtil.extractAccessTokenBankIDNBU(val) === bankidNBUData.accessTokens.forErrorResponse406;
  })
  .reply(406)
  .post(baseUrls.resource.path.info)
  .matchHeader('Authorization', function (val) {
    return appUtil.extractAccessTokenBankIDNBU(val) === bankidNBUData.accessTokens.forErrorResponse501;
  })
  .reply(501);


module.exports.bankidNBUMock = bankidNBUMock;
