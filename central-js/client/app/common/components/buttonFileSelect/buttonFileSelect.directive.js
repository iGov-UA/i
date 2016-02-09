'use strict';

angular.module('app')
  .directive('buttonFileSelect', [
      '$parse',
      function ($parse) {
        return {
          scope: {
            ngDisabled: '=',
            onFileSelect: '&'
          },
          templateUrl: 'app/common/components/buttonFileSelect/buttonFileSelect.directive.html',
          link: function buttonFileSelect(scope, element) {
            var fileField = element.find('input[type="file"]');
            fileField.bind('change', function (event) {
              if (event.target.files && event.target.files.length > 0) {
                $parse(scope.onFileSelect)({
                  $files: event.target.files
                });
              }
            });

            element.find('>button').bind('click', function () {
              fileField[0].click();
            });
          },
          transclude: true
        }
      }
    ]
  );
