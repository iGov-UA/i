var request = require('request');
var config = require('../../config/environment');
var url = require('url');

module.exports.sync = function (inn, callback) {
  var url = getURL(config, '/subject/syncSubject');
  return request.get({
    url: url,
    auth: {
      username: config.activiti.username,
      password: config.activiti.password
    },
    qs: {
      sINN: inn
    }
  }, callback);
};

var getURL = function (config, pathname) {
  return url.format({
    protocol: config.activiti.protocol,
    hostname: config.activiti.hostname,
    pathname: config.activiti.path + pathname
  });
};
