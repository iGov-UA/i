'use strict';
angular.module('dashboardJsApp').directive('fileField', function() {
  return {
    require: 'ngModel',
    restrict: 'E',
    link: function(scope, element, attrs, ngModel, Modal) {
      var fileField = element.find('input');
      var nMaxFileSizeLimit = 10; // max upload file size = 10 MB
      var aAvailableFileExtensions = ["bmp", "gif", "jpeg", "jpg", "png", "tif", "doc", "docx", "odt", "rtf", "pdf"
        , "xls", "xlsx", "xlsm", "xml", "ods", "sxc", "wks", "csv", "zip", "rar", "7z", "p7s"];


      fileField.bind('change', function(event) {
        scope.$apply(function() {
          if (event.target.files && event.target.files.length > 0) {
            var aFilteredFiles = [];
            for(var nFileIndex = 0; nFileIndex < event.target.files.length; nFileIndex++){
              var sFileName = event.target.files[nFileIndex].name;
              var nFileSize = event.target.files[nFileIndex].size;
              var message = null;
              if (nFileSize > (nMaxFileSizeLimit * 1024 * 1024)){
                console.warn("File " + sFileName + " is very big for upload");
                message = "Розмір завантажуємого файлу " + sFileName + " " + (nFileSize / (1024 * 1024)).toFixed(1) + " МБайт перевищує допустимий.\n " +
                  "Для завантаження дозволяються файли розміром не більше " + nMaxFileSizeLimit + " МБайт.";
              } else if (!verifyExtension(sFileName)) {
                console.warn("File " + sFileName + "is not supported");
                var extList = convertAvailableExtensionArrayToString();
                message = "Не підтримуємий тип файлу. Для завантаження допускаються файли лише наступних типів: " + extList;
              } else {
                console.log("File " + sFileName + " validation successfully");
                message = null;
                aFilteredFiles.push(event.target.files[nFileIndex]);
              }
            }
            if(aFilteredFiles.length > 0){
              scope.upload(event.target.files, attrs.name);
            }
          }
          scope.errorMessage = message;
        });
      });

      function verifyExtension (sFileNameForCheck){
        var ext = sFileNameForCheck.split('.').pop().toLowerCase();
        for (var i = 0; i < aAvailableFileExtensions.length; i++){
          if (ext === aAvailableFileExtensions[i]){
            return true;
          }
        }
        return false;
      };

      function convertAvailableExtensionArrayToString (){
        var resultString = null;
        for(var i = 0; i < aAvailableFileExtensions.length; i++){
          if (i === 0){
            resultString = aAvailableFileExtensions[i];
            if (aAvailableFileExtensions.length > 1){
              resultString = resultString + ", ";
            } else {
              resultString = resultString + ".";
            }
          } else if (i === aAvailableFileExtensions.length - 1){
            resultString = resultString + aAvailableFileExtensions[i] + ".";
          } else {
            resultString = resultString + aAvailableFileExtensions[i] + ", ";
          }
        }
        return resultString;
      };

      fileField.bind('click', function(e) {
        e.stopPropagation();
      });
      element.bind('click', function(e) {
        e.preventDefault();
        fileField[0].click();
      });
    },
    template: '<form>' +
                '<button type="button" ng-disabled="isFileProcessUploading.bState" ng-class="{\'btn-igov\':field && field.value, \'btn-link attach-btn\':!field, \'btn-default\':field && !field.value}" class="btn">' +
                  '<span ng-disabled="isFormPropertyDisabled(item)">{{field && field.value ? "Завантажити iнший файл" : "Завантажити файл"}}</span>' +
                  '<input type="file" style="display:none" ng-disabled="isFormPropertyDisabled(item)">' +
                '</button>' +
                '<span ng-if="item.fileName || field.fileName">Файл: <label>{{item.fileName || field.fileName}}</label></span>' +
                '<br>' +
                '<span ng-if="field.signInfo">Пiдпис: <label>{{field.signInfo.customer.signatureData.name || field.signInfo.name}}</label></span>' +
                '<span ng-if="errorMessage" style="color: red">{{errorMessage}}</span>' +
              '</form>',
    replace: true,
    transclude: true
  };
});
