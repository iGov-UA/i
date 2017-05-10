'use strict';

var activiti = require('../../components/activiti'),
    NodeCache = require("node-cache");


var cache = new NodeCache();
var cacheTtl = 1800; // 30min

var buildKey = function (params) {
  var key = 'BPs';
  if (params) {
    for (var k in params) {
      key += '&' + k + '=' + params[k];
    }
  }
  return key;
};

exports.getDocumentStepRights = function (req, res) {
  var user = JSON.parse(req.cookies.user);
  var query = {
    sLogin: user.id,
    nID_Process: req.query.nID_Process
  };
  activiti.get({
    path: '/common/document/getDocumentStepRights',
    query: query
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result)
  })
};

exports.getDocumentStepLogins = function (req, res) {
  activiti.get({
    path: '/common/document/getDocumentStepLogins',
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

exports.getBPs_ForReferent = function (req, res) {
  cache.get(buildKey(req.query), function (error, value) {
    if (value) {
      res.send(value);
    } else {
      var callback = function (error, statusCode, result) {
        if (!error) {
          cache.set(buildKey(req.query), result, cacheTtl);
          res.statusCode = statusCode;
          res.send(result);
        } else {
          console.error(error);
        }
      };
      activiti.get({
        path: 'subject/group/getBPs_ForReferent',
        query: req.query
      }, callback)
    }
  })
};

exports.setDocument = function (req, res) {
  var user = JSON.parse(req.cookies.user);
  activiti.get({
    path: '/action/task/setDocument',
    query: {
      sID_BP: req.query.sID_BP,
      sLogin: user.id
    }
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  })
};

exports.getProcessSubjectTree = function (req, res) {
  activiti.get({
    path: '/subject/process/getProcessSubjectTree',
    query: req.query
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  })
};

exports.delegateDocument = function (req, res) {
  activiti.get({
    path: 'common/document/delegateDocumentStepSubject',
    query: req.query
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  })
};

exports.getDocumentSubmittedUnsigned = function (req, res) {
  activiti.get({
    path: 'common/document/getDocumentSubmitedUnsigned',
    query: req.query
  }, function (error, statusCode, result) {
    if(!error) {
      res.statusCode = statusCode;
      res.send(result)
    }
  })
};


exports.removeDocumentSteps = function (req, res) {
  activiti.get({
    path: 'common/document/removeDocumentSteps',
    query: req.query
  }, function (error, statusCode, result) {
    if(!error) {
      res.statusCode = statusCode;
      res.send(result)
    }
  })
};
