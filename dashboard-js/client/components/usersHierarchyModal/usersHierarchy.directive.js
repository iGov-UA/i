'use strict';

angular.module('dashboardJsApp')
  .directive('usersHierarchyModal', ['$window', function () {
  return {
    restrict: 'E',
    link: function (scope, element, attrs) {
      scope.dialogStyle = {};
      if (attrs.width)
        scope.dialogStyle.width = attrs.width;
      if (attrs.height)
        scope.dialogStyle.height = attrs.height;
    },
    templateUrl: 'components/usersHierarchyModal/usersHierarchy.template.html'
  };
}])
  .directive('compileTemplate', function ($compile, $parse) {
  return {
    link: function (scope, element, attr) {
      var parsed = $parse(attr.ngBindHtml);

      function getStringValue() {
        return (parsed(scope) || '').toString();
      }

      scope.$watch(getStringValue, function () {
        $compile(element, null, -9999)(scope);
      });
    }
  }
});
