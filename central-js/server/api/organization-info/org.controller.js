'use strict';
var request = require('request'),
    config = require('../../config/environment');

module.exports.getOrganization = function (req, res) {
  var downloadURL = 'https://opendatabot.com/api/v1/company/' + req.query.code;
  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };

  request.get({
    url: downloadURL,
    qs: {
      apiKey: config.databot.key
    },
    json: true
  }, callback);
};
