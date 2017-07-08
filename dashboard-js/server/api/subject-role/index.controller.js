'use strict';
var _ = require('lodash');
var activiti = require('../../components/activiti');

module.exports.getSubjectGroups = function (req, res) {
  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };

  var updatedQuery = req.query;

  if(updatedQuery.sID_SubjectRole === 'ExecutorDepart') {
    updatedQuery.sSubjectType = 'Organ';
      delete updatedQuery.sID_SubjectRole;
  } else if(updatedQuery.sID_SubjectRole === 'Executor') {
    updatedQuery.sSubjectType = 'Human';
      delete updatedQuery.sID_SubjectRole;
  }

  var options = {
    path: 'subject/group/getSubjectGroupsTree',
    query: updatedQuery
  };
  activiti.get(options, callback)
};
