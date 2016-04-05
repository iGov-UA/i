'use strict';
angular.module('dashboardJsApp').directive('signInfoDialog', [
  function (scope, element, attrs) {
    return {
      restrict: 'E',
      templateUrl: 'app/tasks/dialogs/signInfoDialog.html',
      replace: true,
      transclude: true,
      link: function (scope, element, attrs) {
        scope.dialogStyle = {};
        if (attrs.width)
          scope.dialogStyle.width = attrs.width;
        if (attrs.height)
          scope.dialogStyle.height = attrs.height;

        scope.hideSignInfoModal = function () {
          scope.checkSignState.show = false;
        };
      }
    };
  }
]);
