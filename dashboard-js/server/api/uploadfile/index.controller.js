'use strict';

var activiti = require('../../components/activiti'),
    proxy = require('../../components/proxy');

exports.uploadFile = function (req, res) {
  var options = {
    url: activiti.getRequestURL({
      path: 'object/file/setProcessAttach',
       query: {
         nID_Process:req.query.nID_Process,
         sID_Field:req.query.sID_Field,
         sFileNameAndExt:req.query.sFileNameAndExt,
         url:''
       }
    })
  };
  req.url='';
  proxy.upload(req, res, options.url, function (error) {
    res.status(500).send({ error: error });
  });
};
