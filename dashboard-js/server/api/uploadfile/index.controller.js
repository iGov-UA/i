'use strict';

var activiti = require('../../components/activiti'),
    proxy = require('../../components/proxy');

exports.uploadFile = function (req, res) {
  var qs = {
    sID_Field:req.query.sID_Field,
    sFileNameAndExt:req.query.sFileNameAndExt
  };

  if(req.query.nID_Process) {
    qs['nID_Process'] = req.query.nID_Process;
  } else {
    qs['sID_StorageType'] = 'Redis'
  }
  qs['url'] = '';

  var options = {
    url: activiti.getRequestURL({
      path: 'object/file/setProcessAttach',
       query: qs
    })
  };
  req.url='';

  proxy.upload(req, res, options.url, function (error) {
    res.status(500).send({ error: error });
  });
};
