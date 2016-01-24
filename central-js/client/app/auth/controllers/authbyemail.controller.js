angular.module('auth')
  .controller(
    'AuthByEmailVerifyController',
    function ($rootScope, $scope, $location, $state, ServiceService, ErrorsFactory) {

      $scope.authorizeByEmail = function (authEmailData) {
        ServiceService.verifyContactEmail(authEmailData).then(function(response){
          if (response.bVerified == "true") {
            $state.go('index.auth.email.submit', authEmailData);
          } else {
            ErrorsFactory.push({
              type: "danger",
              text: "Невалідна електронна адреса"
            });
          }
        });
      };

  }).controller(
    'AuthByEmailSubmitController',
    function ($rootScope, $scope, $location, $state, ServiceService, ErrorsFactory, $stateParams) {

      $scope.authEmailData = {
        email: $stateParams.email
      };

      $scope.authorizeByEmail = function (authEmailData) {
        ServiceService.verifyContactEmailAndCode(authEmailData).then(function(response){
          if (response.bVerified == "true") {
            console.log("true");
            // TODO
          } else {
            ErrorsFactory.push({
              type: "danger",
              text:  "Email is not valid"
            });
          }
        });
      };

      $scope.editEmail = function () {
        $state.go('index.auth.email.validate');
      };
  });
