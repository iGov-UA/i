angular.module('app').directive('serviceAuthBlock', function ($state, $location, bankidProviders, $q, $http) {
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

      scope.mobileIdSubmit = function(callback) {
        
        var cb = callback || angular.noop;
        var deferred = $q.defer();
        $http.post('/api/mobileid', {
          msisdn: this.inputPhone
        }).success(function (data) {
          console.log (data)
            deferred.resolve(data);
            return cb();
        }).error(function (err) {
            deferred.reject(err);
            console.log (err)
            return cb(err);
        }.bind(this));

        return deferred.promise;
        
        this.inputPhone = '';

      };

      scope.getBankIdAuthUrl = function (provider) {
        if (provider.auth == 'BankID') {
          return $location.protocol() + '://' + $location.host() + ':' + $location.port()
            + '/auth/bankID?bank=' + provider.key + '&link=' + scope.redirectUri;
        } else if (provider.auth == 'BankID-NBU') {
          return $location.protocol() + '://' + $location.host() + ':' + $location.port()
            + '/auth/bankid-nbu?bank=' + provider.key + '&link=' + scope.redirectUri;
        }
      }
    }
  };
});
