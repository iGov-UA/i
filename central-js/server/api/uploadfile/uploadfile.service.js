var url = require('url')
  , request = require('request')
  , FormData = require('form-data')
  , activiti = require('../../components/activiti')
  , config = require('../../config/environment');

var apiURLS = {
  upload: '/object/file/upload_file_to_redis',
  download: '/object/file/download_file_from_redis_bytes'
};

function fixHost(sHost){
  if(sHost){
    sHost = sHost + '/service'
  }
  return sHost;
}

module.exports.getAPIEndpoints = function () {
  return apiURLS;
};

/**
 * Method can upload either request, or text, or file to redis
 * @param contentToUpload object {name, request|text|file, [options : {filename, contentType}]}
 * @param callback is called on response
 * @param sHost [optional]. If it's not specified, url from central host is used
 */
module.exports.upload = function (contentToUpload, callback, sHost) {
  activiti.uploadContent(apiURLS.upload, {}, contentToUpload, function (error, response, body) {
//code = "SYSTEM_ERR"
//message = "Could not parse multipart servlet request; nested exception is org.apache.commons.fileupload.FileUploadBase$IOFileUploadException: Processing of multipart/form-data request failed. Stream ended unexpectedly"
    var object;
    if(body){
      if(body.match){
        var fileID = body.match(/^([\d\w]{8}-[\d\w]{4}-[\d\w]{4}-[\d\w]{4}-[\d\w]{12})/);
        if(fileID){
          object = {fileID: body};
        }
      } else {
        object = body;
      }
    }
    callback(error, response, object);
  }, fixHost(sHost));
};

//https://test.region.igov.org.ua/wf/service/object/file/download_file_from_redis_bytes?key=2fad23b1-8ee7-445f-8677-54c0764bc80f
module.exports.prepareDownload = function(fileID, sHost, session){
  return activiti.buildGET(apiURLS.download, {key: fileID}, fixHost(sHost), session);
};

module.exports.download = function (fileID, callback, sHost, session) {
  activiti.get(apiURLS.download, {key: fileID}, callback, fixHost(sHost), session);
};

module.exports.downloadBuffer = function (fileID, callback, sHost, session) {
  activiti.get(apiURLS.download, {key: fileID}, callback, fixHost(sHost), session, true);
};

module.exports.uploadHTMLForm = function (fullUploadURL, formToUpload, headers, callback) {
  var form = new FormData();
  form.append('file', formToUpload, {
    contentType: 'text/html'
  });

  var requestOptionsForUploadContent = {
    url: fullUploadURL,
    headers: _.merge(headers, form.getHeaders()),
    formData: {
      file: formToUpload
    },
    json: true
  };

  request.post(requestOptionsForUploadContent, function (error, response, body) {
    if (!body)
      callback('Unable to sign a file. bankid.privatbank.ua return an empty response', null);
    else if (error || (error = body.error)) {
      callback(error, null);
    } else {
      callback(null, body);
    }
  });

};
