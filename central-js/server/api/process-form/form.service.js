var uploadFileService = require('../uploadfile/uploadfile.service');

module.exports.saveForm = function (sHost, data, callback) {
  uploadFileService.upload([{
    name: 'file',
    options: {
      filename: 'formData.json'
    },
    text: JSON.stringify(data)
  }], callback, sHost);
};
