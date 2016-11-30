'use strict';
var _ = require('lodash');
var activiti = require('../../components/activiti');
var environmentConfig = require('../../config/environment');

var config = environmentConfig.activiti;
var request = require('request');
var catalogController = require('../catalog/catalog.controller.js');
var NodeCache = require("node-cache");

var nodeCache = new NodeCache({stdTTL: 10800, checkperiod: 11000});//Chache for 3 hours
var sHost = config.protocol + '://' + config.hostname + config.path;

var buildUrl = function (path) {
  return sHost + path;
};

module.exports.index = function (req, res) {
  var apiURL = '/action/item/getService?nID=' + req.query.nID
    , callback = function (error, response, body) {
    if (error) {
      res.statusCode = 500;
      res.send(error);
    } else {
      res.statusCode = response.statusCode;
      nodeCache.set(apiURL, body);
      res.send(body);
    }
  };

  nodeCache.get(apiURL, function (err, value) {
    if (!err) {
      if (value == undefined) {
        return activiti.sendGetRequest(req, res, apiURL, null, callback);
      } else {
        return res.send(value);
      }
    } else {
      console.log('Error during get from cache the getService: ', err);
    }
  });
  //activiti.sendGetRequest(req, res, '/action/item/getService?nID=' + req.query.nID);
};

module.exports.getServiceFeedback = function (req, res) {
  var url = sHost + '/subject/message/getFeedbackExternal';
  var data = req.query;

  var callback = function(error, response, body) {
    res.send(body);
    res.end();
  };

  return request.get({
    'url': url,
    'auth': {
      'username': config.username,
      'password': config.password
    },
    'qs': {
      'sID_Token': data.sID_Token || 123,
      'nID_Service': req.params.nID,
      'nID': data.sID_Order,
      'nID__LessThen_Filter': data.nID__LessThen_Filter,
      'nRowsMax': data.nRowsMax
    }
  }, callback);
};

module.exports.postServiceFeedback = function (req, res) {
  var url = sHost + '/subject/message/setFeedbackExternal';
  var data = req.body;
  var nID_Subject = (activiti.bExist(req.session) && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) ? req.session.subject.nID : null;

  var callback = function(error, response, body) {
    res.send(body);
    res.end();
  };

  return request.post({
    'url': url,
    'auth': {
      'username': config.username,
      'password': config.password
    },
    'qs': {
      'nID': data.nID,
      'nID_Subject':  nID_Subject || 0,
      'sID_Token': data.sID_Token,
      'sID_Source': data.sID_Source,
      'sAuthorFIO': data.sAuthorFIO,
      'sMail': data.sMail || ' ',
      'sHead': data.sHead || ' ',
      'sBody': data.sBody,
      'nID_Rate': data.nID_Rate,
      'nID_Service': req.params.nID,
      'sAnswer': data.sAnswer || '',
      'sPlace': data.sPlace,
      'sEmployeeFIO': data.sEmployeeFIO
    }
  }, callback);
};

module.exports.postServiceFeedbackAnswer = function (req, res) {
  var url = sHost + '/subject/message/setFeedbackAnswerExternal';
  var data = req.body;
  var nID_Subject = (activiti.bExist(req.session) && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) ? req.session.subject.nID : null;

  var callback = function(error, response, body) {
    res.send(body);
    res.end();
  };
//TODO review sID_Token
  return request.post({
    'url': url,
    'auth': {
      'username': config.username,
      'password': config.password
    },
    'qs': {
      'sID_Token': null,//data.sID_Token,
      'sBody': data.sBody,
      'nID_SubjectMessageFeedback': data.nID_SubjectMessageFeedback,
      'nID_Subject': nID_Subject,
      'bSelf': false,
      'sAuthorFIO': data.sAuthorFIO
    }
  }, callback);
};

module.exports.getServiceStatistics = function (req, res) {
  var apiURL = '/action/event/getStatisticServiceCounts?nID_Service=' + req.params.nID
    , callback = function (error, response, body) {
    if (error) {
      res.statusCode = 500;
      res.send(error);
    } else {
      res.statusCode = response.statusCode;
      nodeCache.set(apiURL, body);
      res.send(body);
    }
  };

  nodeCache.get(apiURL, function (err, value) {
    if (!err) {
      if (value == undefined) {
        return activiti.sendGetRequest(req, res, apiURL, null, callback);
      } else {
        return res.send(value);
      }
    } else {
      console.log('Error during get from cache the getStatisticServiceCounts: ', err);
    }
  });
  //activiti.sendGetRequest(req, res, '/action/event/getStatisticServiceCounts?nID_Service=' + req.params.nID, null, callback);
  //activiti.sendGetRequest(req, res, '/action/event/getStatisticServiceCounts?nID_Service=' + req.params.nID);
};


module.exports.getServiceHistoryReport = function (req, res) {
  var params = req.params;
  params = _.extend(params);
  activiti.sendGetRequest(req, res, '/action/event/getServiceHistoryReport?sDateAt=', _.extend(req.query, params))
};

module.exports.setService = function (req, res) {
  var callback = function (error, response, body) {
    catalogController.pruneCache();
    res.send(body);
    res.end()
  };

  var url = buildUrl('/action/item/setService');

  request.post({
    'url': url,
    'auth': {
      'username': config.username,
      'password': config.password
    },
    'qs': {
      'nID_Subject': req.session.subject.nID
    },
    'headers': {
      'Content-Type': 'application/json; charset=utf-8'
    },
    'json': true,
    'body': req.body
  }, callback);
};

module.exports.getPatternFilled = function(req, res){
  var callback = function (error, response, body) {
    res.send(body);
    res.end();
  };

  activiti.getServerRegionHost(req.body.nID_Server, regionHostCallback);

  function regionHostCallback(data){
    var regionHost = data,
        url = regionHost + "/service/object/file/dfs/getPatternFilled";

    request.post(url, {
      auth: {
        "username": config.username,
        "password": config.password
      },
      headers: {
        "content-type": "application/json; charset=utf-8"
      },
      qs: {
        sID_Pattern: req.body.sID_Pattern
      },
      body: req.body.oData,
      json: true
    }, callback);
  }
};

module.exports.removeServiceData = function (req, res) {

  var callback = function (error, response, body) {
    catalogController.pruneCache();
    res.send(body);
    res.end();
  };

  var url = buildUrl('/action/item/removeServiceData');

  request.del({
    'url': url,
    'auth': {
      'username': config.username,
      'password': config.password
    },
    'qs': {
      'nID': req.query.nID,
      'bRecursive': req.query.bRecursive,
      'nID_Subject': req.session.subject.nID
    }
  }, callback);
};
