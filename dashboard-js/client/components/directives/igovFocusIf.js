(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .directive('igovFocusIf', [
      '$parse',
      function ($parse) {
        return {
          link: function igovFocusIfPostLink(scope, element, attrs) {
            scope.$watch(function () {
              return $parse(attrs.igovFocusIf)(scope);
            }, function (newValue) {
              if (newValue) {
                element.focus();
              }
            });
          }
        }
      }
    ])
})();
