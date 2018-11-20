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

      scope.inputPhone = "+380";
      scope.mobileIdSubmit = function(callback) {
        scope.spinner = true;
        
        var cb = callback || angular.noop;
        var deferred = $q.defer();
        scope.statusMessage = "";
        scope.validationMessage = "";
        
        var inputPhone = this.inputPhone;

        var found = inputPhone.match(/(^\+380[0-9]{9}$)/);

        if (found == null) {
          scope.spinner = false;
          scope.statusMessage = "Помилка в номері телефону.";
          return false;          
        }              

        var foundKyivstar = inputPhone.match(/(^\+380(67|68|96|97|98)[0-9]{7}$)/);
        if (foundKyivstar == null) {
          scope.spinner = false;
          scope.statusMessage = "Послуга доступна абонентам Kyivstar.";
          return false;          
        } 

        console.log(this.inputPhone);
        $http.post('/api/mobileid', {
          msisdn: this.inputPhone
          
        }).success(function (data) {
          scope.spinner = false;
          if (data==undefined||data==""){
            scope.statusMessage = "Спробуйте пізніше.";
          } else if (data.statusCode == "502") {
            scope.statusMessage = "Авторизация подтверждена.";
          } else if (data.statusMessage != undefined) {
            scope.statusMessage = String(data.statusMessage);
          } else {
            scope.statusMessage = "Неизвестный статус";
          }
                    
          console.log (data);
          console.log (data.statusCode);
          console.log (data.statusMessage);
          console.log (typeof data.statusMessage);
          
            deferred.resolve(data);
            return cb();
            
        }).error(function (err) {
            scope.spinner = false;
            scope.statusMessage = "Gateway Time-out";
            deferred.reject(err);
            console.log (err)
            return cb(err);
            
        }.bind(this));

        return deferred.promise;
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
