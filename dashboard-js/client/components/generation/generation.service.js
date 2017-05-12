'use strict';

angular.module('dashboardJsApp').service('generationService', function ($q, $http) {
  this.generatePDFFromHTML = function (htmlContent) {
    var body = {
      htmlContent: htmlContent
    };
    return $http.post('./api/generate/pdf', body).then(function (response) {
      return response.data;
    })
  }
});
