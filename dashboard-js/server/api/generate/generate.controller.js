var pdfComponent = require('./../../components/pdf'),
  errors = require('../../components/errors');

exports.convertToPDFAndDownload = function (req, res) {
  var htmlContent = req.body.htmlContent;
  pdfComponent.convertHTMLToPDFStream(htmlContent, function (error, result) {
    if (error) {
      res.send(errors.createError(errors.codes.LOGIC_SERVICE_ERROR, error.message, error))
    } else {
      res.type('application/pdf');
      result.stream.pipe(res);
    }
  })
};

exports.convertToPDFBase64 = function (req, res) {
  var htmlContent = req.body.htmlContent;
  pdfComponent.convertHTMLToPDFStream(htmlContent, function (error, result) {
    if (error) {
      res.send(errors.createError(errors.codes.LOGIC_SERVICE_ERROR, error.message, error))
    } else {
      var pdfStream = result.stream;
      var pdfParts = [];
      pdfStream.on('data', function (d) {
        pdfParts.push(d);
      });
      pdfStream.on('end', function () {
        var pdfContent = Buffer.concat(pdfParts).toString('base64');
        res.send({base64 : pdfContent});
      });
    }
  })
};

var async = require('async');
var activiti = require('../../components/activiti');

exports.convertToPDFBase64ThroughJava = function (req, res) {
  async.waterfall([
    function (callback) {

      pdfComponent.convertHTMLToPDFStream(req.body.htmlContent, function (err, pdf) {
        callback(err, {content: pdf.stream, contentType: 'application/pdf'});
      });
    },
    function (data, callback) {
      activiti.uploadStream({
        path: 'object/file/getBase64EncodedFile',
        stream: data.content,
        headers: {
          'Content-Type': data.contentType
        }
      }, function (error, statusCode, result) {
        error ? res.send(error) : res.status(statusCode).json(result);
      });
    }
  ]);
};


exports.convertToPDFBase64MimeThroughJava = function (req, res) {
  async.waterfall([
    function (callback) {

      pdfComponent.convertHTMLToPDFStream(req.body.htmlContent, function (err, pdf) {
        callback(err, {content: pdf.stream, contentType: 'application/pdf'});
      });
    },
    function (data, callback) {
      activiti.uploadStream({
        path: 'object/file/getBase64MimeEncodedFile',
        stream: data.content,
        headers: {
          'Content-Type': data.contentType
        }
      }, function (error, statusCode, result) {
        error ? res.send(error) : res.status(statusCode).json(result);
      });
    }
  ]);
};

exports.getBase64DecodedFileThroughJava = function (req, res) {
  activiti.uploadStream({
    path: 'object/file/getBase64DecodedFile',
    stream: data.content,
    isMime: req.params.isMime,
    headers: {
      'Content-Type': data.contentType
    }
  }, function (error, statusCode, result) {
    if(error){
      res.send(error);
    } else {
      res.type('application/pdf');
      result.stream.pipe(res);
    }
  });
};
