/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

var activiti = require('../../components/activiti');

exports.setBP = function(req, res){
  var options = {
    url: activiti.getRequestURL({
      path: 'action/task/setBP',
      query: {
        sFileName: req.params.sFileName
      }
    })
  };

  activiti.fileupload(req, res, options);
};

exports.getBP = function(req, res){
  var options = {
    path: 'action/task/getBP',
    contentType: "application/xml",
    query: {
      sID: req.params.sID
    }
  };

  activiti.typedfiledownload(req, res, options);
};

exports.getListBP = function(req, res){
  var options = {
    path: 'action/task/getListBP',
    query: {}
  };

  if(typeof req.query.sID_BP !== 'undefined' && req.query.sID_BP !== null && req.query.sID_BP !== ''){
    options.query.sID_BP = req.query.sID_BP;
  }
  if(typeof req.query.sFieldType !== 'undefined' && req.query.sFieldType !== null && req.query.sFieldType !== ''){
    options.query.sFieldType = req.query.sFieldType;
  }
  if(typeof req.query.sID_Field !== 'undefined' && req.query.sID_Field !== null && req.query.sID_Field !== ''){
    options.query.sID_Field = req.query.sID_Field;
  }

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).json(result);
    }
  });
};

exports.removeListBP = function(req, res){
  var options = {
    path: 'action/task/removeListBP',
    query: {}
  };

  if(typeof req.query.sID_BP !== 'undefined' && req.query.sID_BP !== null && req.query.sID_BP !== ''){
    options.query.sID_BP = req.query.sID_BP;
  }
  if(typeof req.query.sFieldType !== 'undefined' && req.query.sFieldType !== null && req.query.sFieldType !== ''){
    options.query.sFieldType = req.query.sFieldType;
  }
  if(typeof req.query.sID_Field !== 'undefined' && req.query.sID_Field !== null && req.query.sID_Field !== ''){
    options.query.sID_Field = req.query.sID_Field;
  }
  if(typeof req.query.sVersion !== 'undefined' && req.query.sVersion !== null && req.query.sVersion !== ''){
    options.query.sVersion = req.query.sVersion;
  }

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).json(result);
    }
  });
};
