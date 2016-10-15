var express = require('express')
  , request = require('request')
  , mock = require('./user.data.js')
  , FormData = require('form-data')
  , StringDecoder = require('string_decoder').StringDecoder
  , async = require('async')
  , fs = require('fs')
  , path = require('path');

module.exports.scanUpload = function (req, res, next) {

  if (mock.isMockEnabled(req.cookies)) {

    var sHost = req.region.sHost;
    var data = req.body;

    var sURL = sHost + '/service/object/file/upload_file_to_redis';
    console.log("[scanUpload]:sURL=" + sURL);

    var uploadURL = sURL; //data.url
    var documentScans = data.scanFields;

    var uploadResults = [];
    var uploadScan = function (documentScan, callback) {

      var filePath = path.join(__dirname, documentScan.scan.link);

      fs.readFile(filePath, function (err, buffer) {
        if (err) {
          return console.log(err);
        }

        var form = new FormData();
        form.append('file', buffer, {
          filename: documentScan.scan.type + '.' + documentScan.scan.extension
        });

        var requestOptionsForUploadContent = {
          url: uploadURL,
          auth: getAuth(),
          headers: form.getHeaders()
        };

        pipeFormDataToRequest(form, requestOptionsForUploadContent, function (result) {
          uploadResults.push({
            fileID: result.data,
            scanField: documentScan
          });
          callback();
        });

      });

    };

    async.forEach(documentScans, function (documentScan, callback) {
      uploadScan(documentScan, callback);
    }, function (error) {
      if (error) {
        res.status(500).send(error);
      } else {
        res.send(uploadResults);
      }
    });

  } else {
    next();
  }

};

function pipeFormDataToRequest(form, requestOptionsForUploadContent, callback) {
  var decoder = new StringDecoder('utf8');
  var result = {};
  form.pipe(request.post(requestOptionsForUploadContent))
    .on('response', function (response) {
      result.statusCode = response.statusCode;
    }).on('data', function (chunk) {
    if (result.data) {
      result.data += decoder.write(chunk);
    } else {
      result.data = decoder.write(chunk);
    }
  }).on('end', function () {
    callback(result);
  });
}


function getOptions() {
  var config = require('../../config/environment');
  var oConfigServerExternal = config.activiti;

  return {
    protocol: oConfigServerExternal.protocol,
    hostname: oConfigServerExternal.hostname,
    port: oConfigServerExternal.port,
    path: oConfigServerExternal.path,
    username: oConfigServerExternal.username,
    password: oConfigServerExternal.password
  };
}

function getAuth() {
  var options = getOptions();
  return {
    'username': options.username,
    'password': options.password
  };
}
