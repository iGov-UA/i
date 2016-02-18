angular.module('app').directive('serviceAuthBlock', function ($state, $location) {
  return {
    restrict: 'E',
    templateUrl: 'app/auth/auth.directive.html',
    link: function (scope, element, attrs) {

      scope.$location = $location;

      if (attrs.authMethods) {
        scope.authMethods = attrs.authMethods.split(',');
      } else {
        scope.authMethods = false;
      }

      scope.redirectUri = attrs.redirectUri;
      scope.nStep = attrs.nStep;

      scope.loginWithEmail = function () {
        $state.go('index.auth.email.verify', {link: scope.redirectUri});
      }
    }
  };
});
