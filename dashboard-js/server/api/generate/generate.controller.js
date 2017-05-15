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
