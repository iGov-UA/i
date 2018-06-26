'use strict';

var _ = require('lodash');
var activiti = require('../../components/activiti');
var errors = require('../../components/errors');
var environment = require('../../config/environment');
var async = require('async');
var request = require('request');
var fs = require('fs');


var prepareRequests = function (req, options, data) {
  var r = null;
  r = request(activiti.getRequestOptions(options));
  return r;
};

function getPDF(req, res, options) {
  var r = prepareRequests(req, options, res);
  req.pipe(r).on('response', function (response) {
    response.headers['content-type'] = 'application/octet-stream';
    // response.body = res;
    // res.send(response.body);
    console.log('answer 71 ' +  response.body);
  }).pipe(res);

  // res.end();
}

//    var callback = function (error, response, body) {
//      var x = body.toString();
//      var y = body.valueOf();
//
//       console.log('answer 38 ' +   y);
//       // res.send(response);
//        //res.end();
//
//   fs.readFile(x, function(err, body){
//     if(err){
//       console.log('answer 44 ' +  body, err);
//       res.send('5000');
//       res.end();
//
//     }else{
//       res.end(content);
//     }
//   });
// };

module.exports.getDocumentPDF = function (req, res) {

  var updatedQuery = req.query;
  console.log(req);
  console.log(req.query);
  var options = {
    path: 'document/image/getDocumentImageFile',
    query: updatedQuery,
    headers: {
      'Content-Type': 'application/pdf;charset=utf-8'
    }
  };

  getPDF(req, res, options);
};

module.exports.getDocumentImageFileVO = function (req, res) {
  var updatedQuery = req.query;
  var options = {
    path: 'document/image/getDocumentImageFileVO',
    query: updatedQuery
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.send(result)
    }
  });
};

module.exports.setDocumentImageFileSign = function (req, res) {
  var options = {
    path: 'document/image/setDocumentImageFileSign',
    query: {
      nID_DocumentImageFile: req.body.nID_DocumentImageFile,
      sSecret_DocumentImageFile: req.body.sSecret_DocumentImageFile
    },
    headers: {
      'Content-Type': 'application/json'
    }
  };
  activiti.post(options, function (error, statusCode, result) {
    if(error) {
      res.send(error);
    } else {
      res.send(result);
    }
  }, {sSign: req.body.sSign,
    sID_SignType: req.body.sID_SignType,
    sSignData_JSON: req.body.sSignData_JSON});
};

module.exports.getDocumentImageFileSigned = function (req, res) {
  var updatedQuery = req.query;
  var options = {
    path: 'document/image/getDocumentImageFileSigned',
    query: updatedQuery,
    responseType: 'arraybuffer'
  };
  getPDF(req, res, options);
};

module.exports.setDocumentImageFile = function (req, res) {
  var options = {
    path: 'document/image/setDocumentImageFile',
    query: {
      sID_Token: req.body.sID_Token,
      sHash: req.body.sHash
    }
    // ,
    // headers: {
    //   'Content-Type': 'multipart/form-data; boundary=RaNdOmDeLiMiTeR'
    // }
  };
  activiti.post(options, function (error, statusCode, result) {
    if(error) {
      res.send(error);
    } else {
      res.send(result);
    }
  }, {file: req.body.file});
};
