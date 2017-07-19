angular.module('dashboardJsApp').directive('stub', function ($timeout) {
  return {
    restrict: 'A',
    link: function (scope, elem, as) {
      $timeout(function() {
        if(!elem.context.children[0]) {
          angular.element(elem).append('<span>' + elem.context.attributes.stub.nodeValue + '</span>')
        }
      })
    }
  }
});
