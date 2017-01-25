'use strict';
var _ = require('lodash');
var activiti = require('../../components/activiti');

module.exports.getSubjectGroups = function (req, res) {
  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };

  var updatedQuery = req.query;
  updatedQuery.sSubjectType = 'Human';

  var options = {
    path: 'subject/group/getSubjectGroupsTree',
    query: updatedQuery
  };
  activiti.get(options, callback)
};
