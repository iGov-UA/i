var request = require('request')
  , config = require('../../config/environment')
  , proxy = require('../../components/proxy')
  , activiti = require('../../components/activiti');

module.exports.uploadProxy = function (req, res) {
  var sHost = req.region.sHost;
  var sURL;
  if(req.query.sFileNameAndExt) {
    req.query.sFileNameAndExt = encodeURIComponent(req.query.sFileNameAndExt);
    sURL = sHost + '/service/object/file/setProcessAttach?sID_StorageType=Redis' + '&sFileNameAndExt='+ req.query.sFileNameAndExt + '&';
  } else {
    sURL = sHost + '/service/object/file/upload_file_to_redis';
  }
  proxy.upload(req, res, sURL, function(error){
    res.status(500).send({ error: error });
  });
};

module.exports.uploadFileHTML = function (req, res) {
  var sHost = req.region.sHost;
  var auth = activiti.getAuth();

  var callback = function (error, response, body) {
    if (!error) {
      res.json(body);
      res.end();
    } else {
      res.send(error, null, null);
      res.end();
    }
  };

  request.post({
    'url': sHost + '/service/object/file/setProcessAttachText',
    'auth': {
      'username': auth.username,
      'password': auth.password
    },
    'qs': {
      sID_Field: req.body.sID_Field,
      sFileNameAndExt: req.body.sFileNameAndExt
    },
    'headers': {
      'Content-Type': 'text/html; charset=utf-8'
    },
    'body': req.body.sContent
  }, callback);

};
