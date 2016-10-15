angular.module('app')
  .directive('buttonFileUpload', [
    '$parse',
    'FileFactory',
    function ($parse, FileFactory) {
      return {
        scope: {
          oServiceData: '=',
          onFileUploadSuccess: '&'
        },
        transclude: true,
        templateUrl: 'app/common/components/buttonFileUpload/buttonFileUpload.directive.html',
        link: function buttonFileUploadPostLink(scope) {
          scope.file = new FileFactory();

          scope.onFileSelect = function ($files) {
            scope.file.setFiles($files);
            scope.file.upload(scope.oServiceData);
          };

          scope.$watch('file.isUploading', function (newValue, oldValue) {
            if (newValue === false && oldValue === true) {
              $parse(scope.onFileUploadSuccess)({
                $file: scope.file
              })
            }
          })
        }
      }
    }
  ]);
