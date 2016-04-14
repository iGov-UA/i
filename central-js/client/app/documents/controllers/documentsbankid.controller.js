angular.module('documents').controller('DocumentsBankIdController', function ($scope, $state, $location, $window, UserService) {
  $scope.authProcess = false;

  function getRedirectURI() {
    var stateForRedirect = $state.href('index.documents.user', {error: ''});
    return $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
  }

  $scope.loginWithBankId = function () {
    $window.location.href = './auth/bankID?link=' + getRedirectURI();
  };

  $scope.loginWithEds = function () {
    var stateForRedirect = $state.href('index.documents.user', {error: ''});
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/eds?link=' + getRedirectURI();
  };

  $scope.getRedirectUrl = getRedirectURI;

  $scope.getAuthMethods = function () {
    return "BankID,EDS,mpbds,KK"
  };

  $scope.loginWithEmail = function () {
    $state.go('index.auth.email.verify', {link: getRedirectURI()});
  };

  $scope.loginWithSoccard = function () {
    $window.location.href = './auth/soccard?link=' + getRedirectURI()();
  };

  if ($state.is('index.documents.bankid')) {
    if (!$state.params.error) {
      UserService.isLoggedIn().then(function () {
        $scope.authProcess = true;
        return $state.go('index.documents.content').catch(function (fallback) {
          //throw new Error(fallback.error);
          $scope.authProcess = false;
        }).finally(function () {
          $scope.authProcess = false;
        });
      });
    }
  }
});
