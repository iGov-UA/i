var request = require('request')
  , config = require('../../config/environment')
  , proxy = require('../../components/proxy')
  , activiti = require('../../components/activiti');

module.exports.uploadProxy = function (req, res) {
  var sHost = req.region.sHost;
  var sURL = sHost + '/service/object/file/upload_file_to_redis';

  proxy.upload(req, res, sURL, function(error){
    res.status(500).send({ error: error });
  });
};
