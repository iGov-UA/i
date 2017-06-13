'use strict';
angular.module('dashboardJsApp').directive('fileField', function($modal, $http, generationService) {
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
        $http.get('http://127.0.0.1:9005/TWAIN@Web/').success(function (data) {
          if(data){
            scanDocument();
          } else {
            alert('Для роботи зі сканером необхідно встановити программу TWAIN@Web')
          }
        }).error(function (err) {
          alert('Для роботи зі сканером необхідно встановити программу TWAIN@Web')
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
          var aFiles = [];
          var base64content = oScanResult.downloadFiles["0"].base64;

          var oScannedFile = generationService.getFileFromBase64(base64content, oScanResult.downloadFiles["0"].file, 'image/jpeg');

          aFiles.push(oScannedFile);
          scope.upload(aFiles, attrs.name);

        });
      }
    },
    templateUrl: 'app/tasks/form-fields/file-field.html',
    replace: true,
    transclude: true
  };
});
