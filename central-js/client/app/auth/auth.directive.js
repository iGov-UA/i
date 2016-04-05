angular.module('app').directive('serviceAuthBlock', function ($state, $location) {
  return {
    restrict: 'A',
    transclude: true,
    templateUrl: 'app/auth/auth.directive.html',
    scope: {
      redirectUri: '@',
      authMethods: '@'
    },
    link: function (scope, element, attrs) {
      scope.$location = $location;

      scope.loginWithEmail = function () {
        $state.go('index.auth.email.verify', {link: scope.redirectUri});
      }
     }
   };
});
