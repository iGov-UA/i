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
}]);
