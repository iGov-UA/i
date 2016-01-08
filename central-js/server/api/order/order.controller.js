var request = require('request');
var config = require('../../config/environment');
var _ = require('lodash');
var activiti = require('../../components/activiti');

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
    var url = getUrl('/action/event/getHistoryEvent_Service');
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
    var url = getUrl('/action/task/setTaskAnswer_Central');///rest
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

function getUrl(apiURL) {
    var options = getOptions();
    return options.protocol + '://' + options.hostname + options.path + apiURL;
}

module.exports.getCountOrders = function (req, res) {
  var params = req.params;
  if (req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) {
    params = _.extend(params, {nID_Subject: req.session.subject.nID});
  }
  activiti.sendGetRequest(req, res, '/services/getCountOrders', _.extend(req.query, params));
};
