var nock = require('nock')
  , url = require('url')
  , urlencode = require('urlencode')
  , appData = require('./../../app.data.spec')
  , bankidNBUData = require('./bankid.data.spec')
  , bankidNBUUtil = require('./bankid.util');

var baseUrls = bankidNBUUtil.getBaseURLs();

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

var pathFromURL = function (urlString) {
  return urlString.split(/\?/).filter(function (item, i) {
    return i == 0
  }).reduce(function (previous, item) {
    return item
  })
};

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
  .reply(200, appData.token, headers)
  .post(baseUrls.resource.path.info)
  .reply(200, bankidNBUData.customerData, headers);


module.exports.bankidNBUMock = bankidNBUMock;
