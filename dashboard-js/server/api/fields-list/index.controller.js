'use strict';
var _ = require('lodash');
var activiti = require('../../components/activiti');

module.exports.getFieldList = function (req, res) {
  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };
  var options = {
    path: 'action/task/getFields',
    query: req.query
  };
  activiti.get(options, callback)
};
