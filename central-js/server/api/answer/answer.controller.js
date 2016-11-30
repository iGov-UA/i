var request = require('request');
var parseString = require('xml2js').parseString;
var config = require('../../config/environment');


function getOptions(req) {
  return {
    protocol: config.activiti.protocol,
    hostname: config.activiti.hostname,
    port: config.activiti.port,
    path: config.activiti.path,
    username: config.activiti.username,
    password: config.activiti.password
  };
}

module.exports.getDecrypted = function (req, res) {
  var qOptions = getOptions(req);
  var fileName = req.query.fileName || 'file.txt';
  var url = qOptions.protocol + '://' + qOptions.hostname + qOptions.path + '/object/file/download_file_from_redis_bytes';
  var options = {
    url: url,
    json: true,
    qs: {key: req.query.signedFileID},
    auth: {username: qOptions.username, password: qOptions.password}
  };

  request(options, function (err, data) {
    if (data.body.code === 'SYSTEM_ERR') {
      res.end();
      return;
    }

    res.writeHead(200, {
      'Content-Type': 'application/force-download',
      'Content-disposition': 'attachment; filename=' + fileName,
      'x-filename': fileName
    });
    res.end(clarifyXML(data.body));
  });
};

module.exports.getJSON = function(req, res){
  var qOptions = getOptions(req);
  var fileName = req.query.fileName || 'file.txt';
  var url = qOptions.protocol + '://' + qOptions.hostname + qOptions.path + '/object/file/download_file_from_redis_bytes';
  var options = {
    url: url,
    json: true,
    qs: {key: req.query.signedFileID},
    auth: {username: qOptions.username, password: qOptions.password}
  };

  request(options, function (err, data) {
    if (data.body.code === 'SYSTEM_ERR') {
      res.end();
      return;
    }

    parseString(clarifyXML(data.body), function (err, result) {
      res.send(result);
    });
  });
};

function clarifyXML(xml){
  var firstRe = /<?xml/mg;
  var lastRe = /<\/DECLAR>/m;

  var firstIndex = xml.search(firstRe);
  var lastIndex = xml.match(lastRe);
  var lastStrip = xml.substr(lastIndex.index + lastIndex[0].length, xml.length);

  return xml.replace(xml.substr(0, firstIndex-2), '').replace(lastStrip, '');
}