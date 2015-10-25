var request = require('request');
var soccardUtil = require('./soccard.util');

module.exports.getUser = function (options, callback) {
  var config = require('../../config/environment');
  var infoURL = soccardUtil.getInfoURL(config);

  //GET /api/info HTTP/1.1
  //Host: test.kyivcard.com.ua
  //SocCard-API-Access-Token: NjM1NDhl...IwMDBlNWQ
  //SocCard-API-Version: 1.0
  //SocCard-API-Transaction-ID: de82c107e5b105c493239eb90f46c735
  //SocCard-API-Signature: ZGIwOTA...GUwZjZjZjI =
  var headers = {};

  soccardUtil.addAccessTokenHeader(headers, options.accessToken);
  soccardUtil.addAPIVersionTokenHeader(headers, config);
  soccardUtil.addTransactionHeader(headers);
  soccardUtil.addSignHeader(headers, config, headers['SocCard-API-Transaction-ID'],
      'GET', infoURL, '', ''
  );

  request.get({
    url: infoURL,
    headers : headers
  }, function (error, response, body) {
    //{
    //  "firstName"
    //:
    //  "Костянтин", "secondName"
    //:
    //  "Анатолійович", "lastName"
    //:
    //  "Ребров", "email"
    //:
    //  "user@example.com", "activeCard"
    //:
    //  "2300273165600897", "personNumber"
    //:
    //  "0100483165600018"
    //}

    callback(error, body);
  });

};
