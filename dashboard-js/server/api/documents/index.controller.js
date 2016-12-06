'use strict';

var activiti = require('../../components/activiti');

exports.getDocumentStepRights = function (req, res) {
  var user = JSON.parse(req.cookies.user);
  var query = {
    sLogin: user.id,
    nID_Process: req.query.nID_Process
  };
  activiti.get({
    path: '/action/task/getDocumentStepRights',
    query: query
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result)
  })
};

exports.getDocumentStepLogins = function (req, res) {
  activiti.get({
    path: '/action/task/getDocumentStepLogins',
    query: req.query
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  })
};

exports.getProcessSubject = function (req, res) {
  activiti.get({
    path: '/subject/process/getProcessSubject',
    query: req.query
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  })
};
