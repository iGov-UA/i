'use strict';

(function () {

  angular
    .module('dashboardJsApp')
    .directive('igovWhenScrolled', [
      function () {
        return {
          link: function igovWhenScrolledPotLink(scope, element, attrs) {
            var el = element[0];
            element.bind('scroll', function () {
              if (el.scrollTop + el.offsetHeight >= el.scrollHeight - (parseInt(attrs.whenScrolledHeight) || 100))
                scope.$apply(attrs.igovWhenScrolled);
            });
          }
        }
      }
    ])
})();

