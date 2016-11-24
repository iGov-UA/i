'use strict';
var _ = require('lodash');
var activiti = require('../../components/activiti');

module.exports.getSubjectGroups = function (req, res) {
  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };
  var options = {
    path: 'subject/group/getSubjectGroups',
    query: req.query
  };
  activiti.get(options, callback)
};
