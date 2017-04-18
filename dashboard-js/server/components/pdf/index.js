var pdfConversion = require('phantom-html-to-pdf')();

module.exports.convertHTMLToPDFStream = function (htmlContent, callback) {
  var options = {
    html: htmlContent,
    allowLocalFilesAccess: true,
    paperSize: {
      format: 'A4', orientation: 'portrait'
    },
    fitToPage: true,
    customHeaders: [],
    settings: {
      javascriptEnabled: true
    },
    format: {
      quality: 100
    }
  };
  pdfConversion(options, function (err, pdf) {
    if (err) {
      callback(err, null);
    } else {
      callback(null, {stream: pdf.stream});
    }
  });
};
