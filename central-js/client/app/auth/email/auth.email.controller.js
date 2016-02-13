angular.module('auth').controller('AuthByEmailController', function ($window, $scope, $location, $state, ServiceService, ErrorsFactory) {

  $scope.authByEmail = {
    email: '',
    code: '',
    firstName: '',
    middleName: '',
    lastName: ''
  };

  $scope.authorizeByEmail = function (sAuthEmail) {
    var oFuncNote = {sHead: "Авторизація через елетрону адресу", sFunc: "authorizeByEmail"};
    ErrorsFactory.init(oFuncNote, {asParam: ['sAuthEmail: ' + sAuthEmail]});
    ServiceService.verifyContactEmail(sAuthEmail).then(function (oResponse) {
      if (ErrorsFactory.bSuccessResponse(oResponse)) {
        if (oResponse.bVerified === "true") {
          $state.go('index.auth.email.submit', sAuthEmail);
        } else {
          ErrorsFactory.addFail({
            sBody: 'Невалідна електронна адреса!',
            asParam: ['soResponse: ' + JSON.stringify(oResponse)]
          });
        }
      }
    });
  };

  $scope.authorizeByEmailAndCode = function (sAuthEmail) {
    var oFuncNote = {sHead: "Авторизація через елетрону адресу(2)", sFunc: "authorizeByEmail(2)"};
    ErrorsFactory.init(oFuncNote, {asParam: ['sAuthEmail: ' + sAuthEmail]});
    ServiceService.verifyContactEmailAndCode(sAuthEmail).then(function (oResponse) {
      if (ErrorsFactory.bSuccessResponse(oResponse)) {
        if (oResponse.verified) {
          if (oResponse.firstName) {
            $scope.authByEmail.firstName = firstName;
          }
          if (oResponse.middleName) {
            $scope.authByEmail.middleName = middleName;
          }
          if (oResponse.lastName) {
            $scope.authByEmail.lastName = lastName;
          }

          $state.go('index.auth.email.enter');
        } else {
          ErrorsFactory.addFail({
            sBody: 'Невалідна електронна адреса!',
            asParam: ['soResponse: ' + JSON.stringify(oResponse)]
          });
        }
      }
    });
  };

  $scope.editNamesInEmailAuth = function (sAuthEmail) {
    ServiceService.editNamesInEmailAuth(sAuthEmail).then(function (result) {
      $window.location.href = './auth/email';
    });
  };

  $scope.editEmail = function () {
    $state.go('index.auth.email.verify');
  };

  $state.go('index.auth.email.verify');
});
