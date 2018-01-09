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

exports.setAttachment = function (req, res) {
  var query = {
    sFileNameAndExt: req.body.sFileNameAndExt,
    sID_Field: req.body.nID_Attach
  };

  if (req.body.nID_Process) {
    query['nID_Process'] = req.body.nID_Process;
  }

  var options = {
    path: 'object/file/setProcessAttachText',
    query: query,
    headers: {
      'Content-Type': getContentTypeByFileName(req.body.sFileNameAndExt, 'text/html') + ';charset=utf-8'
    }
  };

  activiti.post(options, function (error, statusCode, result) {
    error ? res.send(error) : res.status(statusCode).json(result);
  }, req.body.sContent, false);

};

function getContentTypeByFileName(sFileNameAndExt, defaultType) {
  var ext = sFileNameAndExt.split('.').pop().toLowerCase();
  switch (ext){
    case "pdf": return 'application/pdf';
    case "html": return 'text/html';
    case "bmp": return 'image/bmp';
    case "gif": return 'image/gif';
    case "jpeg": return 'image/jpeg';
    case "jpg": return 'image/jpeg';
    case "png": return 'image/png';
    case "tif": return 'image/tiff';
    case "doc": return 'application/msword';
    case "docx": return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
    case "odt": return 'application/vnd.oasis.opendocument.text';
    case "rtf": return 'application/rtf';
    case "xls": return 'application/excel';
    case "xlsx": return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    case "xlsm": return 'application/vnd.ms-excel.sheet.macroEnabled.12';
    case "xml": return 'text/xml';
    case "ods": return 'application/vnd.oasis.opendocument.spreadsheet';
    case "sxc": return 'application/vnd.sun.xml.calc';
    case "wks": return 'application/vnd.ms-works';
    case "csv": return 'text/csv';
    case "zip": return 'application/zip';
    case "rar": return 'application/x-rar-compressed';
    case "7z": return 'application/x-7z-compressed';
    case "p7s": return 'application/x-pkcs7-signature';
    default: return defaultType ? defaultType : 'application/octet-stream';
  }
}
