angular.module('app').directive('fileField', function () {
  return {
    require: 'ngModel',
    restrict: 'E',
    link: function (scope, element, attrs, ngModel) {
      var fileField = element.find('input');
      var oFile = scope.data.formData.params[ngModel.$name];

      var nMaxFileSizeLimit = 10; // max upload file size = 10 MB
      var aAvailableFileExtensions = ["bmp", "gif", "jpeg", "jpg", "png", "tif", "doc", "docx", "odt", "rtf", "pdf"
        , "xls", "xlsx", "xlsm", "ods", "sxc", "wks", "csv"];


      try {
        console.log('scope.data.formData.params[ngModel.$name].fileName=' + scope.data.formData.params[ngModel.$name].fileName);
        scope.data.formData.params[ngModel.$name].fileName = "123" + scope.data.formData.params[ngModel.$name].fileName;
        console.log('scope.data.formData.params[ngModel.$name].fileName(after)=' + scope.data.formData.params[ngModel.$name].fileName);
      } catch (_) {
        console.log('ERROR:scope.data.formData.params[ngModel.$name].fileName:' + _);
      }

      fileField.bind('change', function (event) {
        scope.$apply(function () {
          if (event.target.files && event.target.files.length > 0) {
            var sFileName = event.target.files[0].name;
            var nFileSize = event.target.files[0].size;
            var message = null;
            if (nFileSize > (nMaxFileSizeLimit * 1024 * 1024)){
              message = "Розмір завантажуємого файлу " + (nFileSize / (1024 * 1024)).toFixed(1) + " МБайт перевищує допустимий.\n " +
                "Для завантаження дозволяються файли розміром не більше " + nMaxFileSizeLimit + " МБайт."
              //alert(message);
              throw new Error(message);
            } else if (!verifyExtension(sFileName)) {
              var extList = convertAvailableExtensionArrayToString();
              message = "Не допустимий тип файлу. \n Для завантаження допускаються файли лише наступних типів: " + extList;
              //alert(message);
              throw new Error(message);
            } else {
              console.log("File validation successfully");
              oFile.setFiles(event.target.files);
              oFile.upload(scope.oServiceData);
              console.log('ngModel.$name=' + ngModel.$name);
            }
          }
        });
      });

      verifyExtension = function(sFileNameForCheck){
        var ext = sFileNameForCheck.split('.').pop().toLowerCase();
        for (var i = 0; i < aAvailableFileExtensions.length; i++){
          if (ext === aAvailableFileExtensions[i]){
            return true;
          }
        }
        return false;
      };

      convertAvailableExtensionArrayToString = function(){
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

      fileField.bind('click', function (e) {
        e.stopPropagation();
      });
      element.bind('click', function (e) {
        e.preventDefault();
        fileField[0].click();
      });
    },
    // todo: Замінити цей темплейт на використування директиви buttonFileUpload
    template: '<p>' +
    ' <button type="button" class="btn btn-success" ng-disabled="data.formData.params[property.id].isUploading"' +
    '  <span class="glyphicon glyphicon-file" aria-hidden="true">' +
    '  </span>' +
    '  <span ng-disabled="data.formData.params[property.id].isUploading">Обрати файл</span>' +
    '  <span class="small-loading" ng-if="data.formData.params[property.id].isUploading"></span>' +
    '  <input type="file" style="display:none"  ng-disabled="data.formData.params[property.id].isUploading">' +
    ' </button>' +
    ' <br/>' +
    ' <label ng-if="data.formData.params[property.id].value">Файл: {{data.formData.params[property.id].fileName}}</label>' +
    ' <br/>' +
    ' <label ng-if="data.formData.params[property.id].value && data.formData.params[property.id].value.signInfo"  class="form-control_"> ' +
    '    Підпис: {{data.formData.params[property.id].value.signInfo.name}} ' +
    ' </label> ' +
    '</p>',
    replace: true,
    transclude: true
  };
});
