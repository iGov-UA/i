'use strict';

angular.module('dashboardJsApp').service('generationService', function ($q, $http, $base64) {
  this.generatePDFFromHTML = function (htmlContent) {
    var body = {
      htmlContent: htmlContent
    };
    return $http.post('./api/generate/pdf', body).then(function (response) {
      return response.data;
    })
  };

  this.getSignedFile = function (sEncodedBase64, sFileName) {
    if(!sFileName || sFileName === ''){
      sFileName = 'document';
    }
    return this.getFileFromBase64(sEncodedBase64, sFileName + ".pdf", 'application/pdf');
  };

  this.getFileFromBase64 = function (sEncodedBase64, sFileNameAndExtension, sMimeType) {
    var byteCharacters = $base64.decode(sEncodedBase64);
    var byteNumbers = new Array(byteCharacters.length);
    for (var i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    var byteArray = new Uint8Array(byteNumbers);
    if(!sFileNameAndExtension || sFileNameAndExtension === ''){
      sFileNameAndExtension = 'document';
    }
    if(!sMimeType || sMimeType === ''){
      sMimeType = 'text/plain';
    }
    return new File([byteArray], sFileNameAndExtension, {type: sMimeType});
  };

  this.getSignedFileLink = function(sEncodedBase64){
    var byteCharacters = $base64.decode(sEncodedBase64);
    var byteNumbers = new Array(byteCharacters.length);
    for (var i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    var byteArray = new Uint8Array(byteNumbers);
    var blob = new Blob([byteArray], {type: 'application/pdf'});
    return (window.URL || window.webkitURL).createObjectURL(blob);
  };
});
