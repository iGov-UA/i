(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .directive('igovFocusIf', [
      '$parse',
      function ($parse) {
        return {
          link: function igovFocusIfPostLink(scope, element, attrs) {
            if ($parse(attrs.igovFocusIf)(scope))
              element.focus();
          }
        }
      }
    ])
})();
