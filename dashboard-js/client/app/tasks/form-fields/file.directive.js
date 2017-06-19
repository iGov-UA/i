'use strict';
angular.module('dashboardJsApp').directive('fileField', function($modal, $http, generationService, Modal, ScannerService, $base64) {
  return {
    require: 'ngModel',
    restrict: 'E',
    link: function(scope, element, attrs, ngModel) {
      var fileField = element.find('input');

      fileField.bind('change', function(event) {
        scope.$apply(function() {
          scope.upload(event.target.files, attrs.name);
        });
      });

      fileField.bind('click', function(e) {
        e.stopPropagation();
      });
      element.find('#upload-button').bind('click', function(e) {
        e.preventDefault();
        fileField[0].click();
      });



      scope.openScanModal = function (item) {
        $http.get(ScannerService.getTwainServerUrl()).success(function (data) {
          if(data){
            scanDocument();
          }
        }).error(function (err) {
          Modal.inform.error()('Сталася помилка при намаганні перевірити підключення до служби TWAIN@Web: ' + JSON.toString(err));
        });

      };

      function scanDocument() {
        var modalInstance = $modal.open({
          animation: true,
          templateUrl: 'components/scanner/scan-modal.html',
          controller: 'ScannerModalCtrl',
          resolve: {}
        });

        modalInstance.result.then(function (oScanResult) {

          $http.get(oScanResult.downloadUrl+'&asBase64=true').success(function (data) {
            uploadFile(data.base64, data.file, getImagesMymeType(data.file));
          })

        });
      }

      function getImagesMymeType(sFileName) {
        var ext = sFileName.split('.').pop().toLowerCase();
        if(ext === 'jpg'){
          return 'image/jpeg';
        } else if (ext === 'bmp'){
          return 'image/bmp';
        } else if (ext === 'tiff'){
          return 'image/tiff'
        } else if (ext === 'pdf'){
          return 'application/pdf'
        }
        return undefined;
      }

      function uploadFile(base64content, fileName, sMimeType) {
        var aFiles = [];
        var oScannedFile = generationService.getFileFromBase64(base64content, fileName, sMimeType);
        aFiles.push(oScannedFile);
        scope.upload(aFiles, attrs.name);
      }
    },
    templateUrl: 'app/tasks/form-fields/file-field.html',
    replace: true,
    transclude: true
  };
});
