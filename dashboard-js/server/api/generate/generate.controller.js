var pdfComponent = require('./../../components/pdf'),
  errors = require('../../components/errors');

exports.convertToPDF = function (req, res) {
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
