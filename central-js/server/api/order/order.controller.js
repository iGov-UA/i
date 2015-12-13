var request = require('request');
var config = require('../../config/environment');

function getOptions() {
    var activiti = config.activiti;

    return {
        protocol: activiti.protocol,
        hostname: activiti.hostname,
        port: activiti.port,
        path: activiti.path,
        username: activiti.username,
        password: activiti.password
    };
}

module.exports.searchOrderBySID = function (req, res) {

    var options = getOptions();
    var url = getUrl('/services/getHistoryEvent_Service');
    var callback = function(error, response, body) {
        res.send(body);
        res.end();
    };

    return request.get({
        'url': url,
        'auth': {
            'username': options.username,
            'password': options.password
        },
        'qs': {
            'nID_Protected': req.params.nID,
            'sToken': req.query.sToken
        }
    }, callback);
};

module.exports.setTaskAnswer = function(req, res) {
    var options = getOptions();
    var url = getUrl('/services/setTaskAnswer_Central', true);///rest
    var callback = function(error, response, body) {
      res.send(body);
      res.end();
    };

    return request.get({
      'url': url,
      'auth': {
        'username': options.username,
        'password': options.password
      },
      'qs': req.body
    }, callback);
};

function getUrl(apiURL, regional) {
    var options = getOptions();
    return (regional ? config.server.sServerRegion : options.protocol + '://' + options.hostname) + options.path + apiURL;
}
