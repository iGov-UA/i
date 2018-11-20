angular.module('app').directive('serviceAuthBlock', function ($state, $location, bankidProviders) {
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
      };

      scope.bankidProvidersList = bankidProviders;

      scope.showBankIdDropdown = false;

      scope.bankIdClick = function () {
        scope.showBankIdDropdown = !scope.showBankIdDropdown;
      };

      scope.getBankIdAuthUrl = function (provider) {
        if (provider.auth == 'BankID') {
          return $location.protocol() + '://' + $location.host() + ':' + $location.port()
            + '/auth/bankID?bank=' + provider.key + '&link=' + scope.redirectUri;
        }
        else if (provider.auth == 'BankID-NBU') {
          $(document).ready(function () {
            $('ul.dropdown-menu > li:last-child').mouseenter(function () {
              $('.errorInfo').css('visibility', 'visible');
            });
            $('ul.dropdown-menu > li:last-child').mouseleave(function () {
              $('.errorInfo').css('visibility', 'hidden');
            });
          });

          /*return $location.protocol() + '://' + $location.host() + ':' + $location.port()
            + '/auth/bankid-nbu?bank=' + provider.key + '&link=' + scope.redirectUri;*/
        }
      }
    }
  };
});
