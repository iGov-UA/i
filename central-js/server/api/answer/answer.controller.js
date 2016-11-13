var request = require('request');
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
    res.end(data.body);
  });
};