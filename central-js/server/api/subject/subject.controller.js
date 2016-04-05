'use strict';

var subjectService = require('./subject.service')
  , activiti = require('./../../components/activiti')
  , _ = require('lodash');

module.exports.getSubjectOrganJoinTaxList = function (req, res) {
  //TODO remove req and res as input
  subjectService.getSubjectOrganJoinTaxList(req, res, '/subject/getSubjectOrganJoinTax', req.query);
};


module.exports.getSubjectOrganJoin = function (req, res) {
  // {nID_SubjectOrgan:1} for test
  activiti.sendGetRequest(req, res, '/subject/getSubjectOrganJoins', _.extend(req.query, req.params));
};

module.exports.getOrganAttributes = function (req, res) {
  var apiReq = activiti.buildRequest(req, '/subject/getSubjectOrganJoinAttributes', _.extend(req.query, req.params));
  apiReq.body = req.body;
  apiReq.json = true;
  activiti.executePostRequest(apiReq, res);
};
